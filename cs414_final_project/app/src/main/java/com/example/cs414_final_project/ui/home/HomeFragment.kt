package com.example.cs414_final_project.ui.home
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs414_final_project.EventResponse
import com.example.cs414_final_project.TicketAdapter
import com.example.cs414_final_project.TicketmasterService
import com.example.cs414_final_project.databinding.FragmentHomeBinding
import com.google.firebase.FirebaseApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {
    private val API_KEY = "VyevAFJ3DWVAO0imxBpAGV7yiwE0pFzI"
    private  val TAG = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketmasterService: TicketmasterService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Retrofit and TicketmasterService
        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.ticketmaster.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        ticketmasterService = retrofit.create(TicketmasterService::class.java)

        // Set up the search button click listener
        binding.btnSearch.setOnClickListener {
            // Get the text from the keyword and city EditText fields
            val keyword = binding.etKeyword.text.toString().trim()
            val city = binding.etCity.text.toString().trim()

            // Check if keyword and city are not empty
            if (keyword.isNotEmpty() && city.isNotEmpty()) {
                // Call the searchEvents function with the keyword and city
                searchEvents(keyword, city)
            } else {
                // Handle the case where keyword or city is empty
                // For example, show a Toast message to the user
                Toast.makeText(requireContext(), "Keyword and city cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun searchEvents(keyword: String, city: String) {
        val sort = "date,asc"
        val call = ticketmasterService.searchEvents(API_KEY, keyword, city, sort)
        call.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Request Successful")
                    val events = response.body()?._embedded?.events ?: emptyList()
                    if (events.isEmpty()) {
                        // Handle no results
                        Toast.makeText(requireContext(), "No events found for the given keyword and city.", Toast.LENGTH_SHORT).show()
                    } else {
                        recyclerView.adapter = TicketAdapter(requireContext(), events)
                    }
                } else {
                    Log.d(TAG, "Request not successful")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e(TAG, "Error occurred while fetching data: ${t.message}", t)
                // Handle failure
            }
        })
    }
}