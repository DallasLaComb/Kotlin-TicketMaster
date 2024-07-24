package com.example.cs414_final_project

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject

class EventDetailsFragment : Fragment() {

    companion object {
        private const val ARG_EVENT_NAME = "event_name"
        private lateinit var listView: ListView
        private lateinit var adapter: EventDetailsAdapter
        private val eventDetailsList = mutableListOf<String>()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_details, container, false)
        listView = view.findViewById(R.id.list_view)
        adapter = EventDetailsAdapter(requireContext(), eventDetailsList)
        listView.adapter = adapter
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeButton = view.findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        arguments?.getString(ARG_EVENT_NAME)?.let { eventName ->
            CoroutineScope(Dispatchers.Main).launch {
                fetchEventDetails(eventName)
            }
        }
    }
    private suspend fun fetchEventDetails(eventName: String) {
        withContext(Dispatchers.IO) {
            try {
                // Retrieve SharedPreferences
                val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
                val descriptionChecked = sharedPreferences.getBoolean("description_checked", false)
                val highlightsChecked = sharedPreferences.getBoolean("highlights_checked", false)
                val accoladesChecked = sharedPreferences.getBoolean("accolades_checked", false)
                // Create the GenerativeModel instance
                val generativeModel = GenerativeModel(
                    modelName = "gemini-pro",
                    apiKey = "AIzaSyC54c7KJDq88ieVvN0fbvWbbw0-7Q5yPWs"
                )
                // Construct the prompt
                val promptBuilder = StringBuilder()
                promptBuilder.append("Can you give me more information on $eventName? Format your answer like a JSON, but as a string where each key is the title of what you are giving more information of and the value is the information. Do not include any special characters, escape sequences, or non-plain text in the values.  Here is what you will be giving more information of: ")
                // Append selected preferences to the prompt
                if (descriptionChecked) {
                    promptBuilder.append(" Description.")
                }
                if (highlightsChecked) {
                    promptBuilder.append(" Highlights.")
                }
                if (accoladesChecked) {
                    promptBuilder.append(" Accolades.")
                }

                // Only call generateContent if at least one preference is checked
                if (descriptionChecked || highlightsChecked || accoladesChecked) {
                    val response = generativeModel.generateContent(promptBuilder.toString())
                    withContext(Dispatchers.Main) {
                        updateEventInfo(response.text ?: "No information available")
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions thrown by the API call
                withContext(Dispatchers.Main) {
                    println("Error fetching event details: ${e.message}")
                }
            }
        }
    }

    private fun updateEventInfo(eventInfo: String) {
        eventDetailsList.clear()
        // Remove newline characters from the string
        val cleanedEventInfo = eventInfo.trim()
            .removePrefix("json")
            .replace("\\n", "")
            .replace("\n", "")
        try {
            // Parse the cleaned string into a JSONObject
            val jsonObject = JSONObject(cleanedEventInfo)
            // Iterate over the keys of the JSONObject
            jsonObject.keys().forEach { key ->
                // Get the value associated with the key
                val value = jsonObject.getString(key)
                // Add the title-description pair to the list
                eventDetailsList.add("$key: $value")
            }
        } catch (e: JSONException) {
            eventDetailsList.add("Error parsing event details: ${e.message}")
        }
        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged()
    }
}