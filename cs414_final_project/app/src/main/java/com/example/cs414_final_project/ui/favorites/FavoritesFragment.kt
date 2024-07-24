package com.example.cs414_final_project.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.cs414_final_project.EventData
import com.example.cs414_final_project.FavoritesAdapter
import com.example.cs414_final_project.R
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoriteEventList: ArrayList<EventData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.lv_favorites)
        favoriteEventList = ArrayList()
        favoritesAdapter = FavoritesAdapter(requireContext(), favoriteEventList)
        listView.adapter = favoritesAdapter

        fetchFavoritesFromFirestore()
    }

    private fun fetchFavoritesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val eventsCollection = db.collection("events")

        eventsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("FavoritesFragment", "Fetching events from Firestore...")
                favoriteEventList.clear()
                for (document in querySnapshot.documents) {
                    val eventData = document.toObject(EventData::class.java)
                    if (eventData != null) {
                        favoriteEventList.add(eventData)
                        Log.d("FavoritesFragment", "Added event: ${eventData.name}")
                    } else {
                        Log.w("FavoritesFragment", "Failed to convert document to EventData: ${document.id}")
                    }
                }
                Log.d("FavoritesFragment", "Fetched ${favoriteEventList.size} events")
                favoritesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("FavoritesFragment", "Error fetching events: $exception")
            }
    }
}