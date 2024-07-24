package com.example.cs414_final_project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TicketAdapter(private val context: Context, private val events: List<Event>): RecyclerView.Adapter<TicketAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.tv_event_name)
        val eventImage: ImageView = itemView.findViewById(R.id.iv_event_image)
        val eventDate: TextView = itemView.findViewById(R.id.tv_event_date)
        val venueName: TextView = itemView.findViewById(R.id.tv_venue_name)
        val venueCity: TextView = itemView.findViewById(R.id.tv_venue_city)
        val venueCity2: TextView = itemView.findViewById(R.id.tv_venue_city_2)
        val venueState: TextView = itemView.findViewById(R.id.tv_venue_state)
        val venueAddress: TextView = itemView.findViewById(R.id.tv_venue_address)
        val venueTicketLink: Button = itemView.findViewById(R.id.btn_see_tickets)
        val priceRange: TextView = itemView.findViewById(R.id.tv_price_range)
        val moreEventInfoButton: AppCompatImageButton = itemView.findViewById(R.id.bu_more_event_info)
        val favoriteButton: AppCompatImageButton = itemView.findViewById(R.id.bu_favorite)

        init {
            venueTicketLink.setOnClickListener {
                val selectedItem = adapterPosition
                if (selectedItem != RecyclerView.NO_POSITION) {
                    val currentEvent = events[selectedItem]
                    val url = currentEvent.url
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
            moreEventInfoButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentEvent = events[position]
                    val currentEventName = currentEvent.name
                    val bundle = Bundle().apply {
                        putString("event_name", currentEventName)
                    }
                    // Use the NavController to navigate to the EventDetailsFragment
                    val navController = (context as AppCompatActivity).findNavController(R.id.nav_host_fragment_activity_main)
                    navController.navigate(R.id.action_navigation_home_to_eventDetailsFragment, bundle)
                }
            }
            favoriteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentEvent = events[position]
                    saveEventToFirestore(currentEvent)
                }
            }
        }

        private fun saveEventToFirestore(event: Event) {
            val db = FirebaseFirestore.getInstance()
            val eventsCollection = db.collection("events")

            // Create a query to check if the event with the same name and date already exists
            val eventName = event.name
            val eventDate = "${event.dates.start.localDate}T${event.dates.start.localTime}"
            val query = eventsCollection
                .whereEqualTo("name", eventName)
                .whereEqualTo("date", eventDate)

            // Execute the query
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        // Event already exists, display a toast message
                        Toast.makeText(context, "Event is already bookmarked.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Event does not exist, add it to Firestore
                        val eventData = hashMapOf(
                            "name" to event.name,
                            "imageUrl" to event.images.maxByOrNull { it.width * it.height }?.url,
                            "date" to "${event.dates.start.localDate}T${event.dates.start.localTime}",
                            "venueName" to (event._embedded.venues.getOrNull(0)?.name ?: ""),
                            "venueCity" to (event._embedded.venues.getOrNull(0)?.city?.name ?: ""),
                            "venueState" to (event._embedded.venues.getOrNull(0)?.state?.name ?: ""),
                            "venueAddress" to (event._embedded.venues.getOrNull(0)?.address?.line1 ?: ""),
                            "ticketUrl" to event.url,
                            "minPrice" to (event.priceRanges?.firstOrNull()?.min?.toString() ?: "N/A"),
                            "maxPrice" to (event.priceRanges?.firstOrNull()?.max?.toString() ?: "N/A")
                        )

                        eventsCollection.add(eventData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Event added to favorites successfully.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(context, "Failed to add event to favorites: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    // Handle the error
                    Toast.makeText(context, "Error checking event in favorites: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ticket_card, parent, false)
        return EventViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventName.text = event.name
        val highestQualityImage = event.images.maxByOrNull { it.width * it.height }?.url
        Glide.with(holder.eventImage.context)
            .load(highestQualityImage)
            .into(holder.eventImage)
        val localDateTime = LocalDateTime.parse("${event.dates.start.localDate}T${event.dates.start.localTime}")
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ h:mm a")
        val formattedDateTime = localDateTime.format(formatter)
        holder.eventDate.text = "Date: " + formattedDateTime
        holder.venueName.text = (event._embedded.venues.getOrNull(0)?.name ?: "") + ", "
        holder.venueCity.text = (event._embedded.venues.getOrNull(0)?.city?.name ?: "") + ", "
        holder.venueCity2.text = event._embedded.venues.getOrNull(0)?.city?.name ?: ""
        holder.venueState.text = event._embedded.venues.getOrNull(0)?.state?.name ?: ""
        holder.venueAddress.text = event._embedded.venues.getOrNull(0)?.address?.line1 +  ", "
        holder.venueTicketLink.text = "See Tickets"

        val priceRange = event.priceRanges?.firstOrNull()
        if (priceRange != null) {
            val minPrice = priceRange.min?.toString() ?: "N/A"
            val maxPrice = priceRange.max?.toString() ?: "N/A"
            holder.priceRange.text = "Price Range: $$minPrice - $$maxPrice"
        } else {
            holder.priceRange.text = "Price Range: Not available"
        }
    }

    override fun getItemCount(): Int = events.size
}
