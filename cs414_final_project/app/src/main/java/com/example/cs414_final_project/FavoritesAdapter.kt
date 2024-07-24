package com.example.cs414_final_project
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
class FavoritesAdapter(private val context: Context, private val events: ArrayList<EventData>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = events.size

    override fun getItem(position: Int): Any = events[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.ticket_card, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val eventData = events[position]

        viewHolder.eventName.text = eventData.name
        Glide.with(viewHolder.eventImage.context)
            .load(eventData.imageUrl)
            .into(viewHolder.eventImage)
        viewHolder.eventDate.text = "Date: " + eventData.date
        viewHolder.venueName.text = eventData.venueName + ", "
        viewHolder.venueCity.text = eventData.venueCity + ", "
        viewHolder.venueCity2.text = eventData.venueCity
        viewHolder.venueState.text = eventData.venueState
        viewHolder.venueAddress.text = eventData.venueAddress + ", "
        viewHolder.venueTicketLink.text = "See Tickets"
        viewHolder.priceRange.text = "Price Range: $${eventData.minPrice} - $${eventData.maxPrice}"

        viewHolder.venueTicketLink.setOnClickListener {
            val url = eventData.ticketUrl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }

        viewHolder.moreEventInfoButton.setOnClickListener {
            val currentEventName = eventData.name
            val bundle = Bundle().apply {
                putString("event_name", currentEventName)
            }
            val navController = (context as AppCompatActivity).findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.action_navigation_favorites_to_eventDetailsFragment, bundle)
        }

        viewHolder.favoriteButton.setOnClickListener {
            val eventName = eventData.name
            val eventDate = eventData.date

            // Query Firestore for the event with the given name and date
            val db = FirebaseFirestore.getInstance()
            val eventsCollection = db.collection("events")
            val query = eventsCollection
                .whereEqualTo("name", eventName)
                .whereEqualTo("date", eventDate)

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        // Delete the matching document
                        document.reference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Event removed from favorites", Toast.LENGTH_SHORT).show()
                                removeEvent(eventData)
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(context, "Failed to remove event: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(context, "Error getting event: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun removeEvent(eventData: EventData) {
        events.remove(eventData)
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) {
        val eventName: TextView = view.findViewById(R.id.tv_event_name)
        val eventImage: ImageView = view.findViewById(R.id.iv_event_image)
        val eventDate: TextView = view.findViewById(R.id.tv_event_date)
        val venueName: TextView = view.findViewById(R.id.tv_venue_name)
        val venueCity: TextView = view.findViewById(R.id.tv_venue_city)
        val venueCity2: TextView = view.findViewById(R.id.tv_venue_city_2)
        val venueState: TextView = view.findViewById(R.id.tv_venue_state)
        val venueAddress: TextView = view.findViewById(R.id.tv_venue_address)
        val venueTicketLink: Button = view.findViewById(R.id.btn_see_tickets)
        val priceRange: TextView = view.findViewById(R.id.tv_price_range)
        val moreEventInfoButton: AppCompatImageButton = view.findViewById(R.id.bu_more_event_info)
        val favoriteButton: AppCompatImageButton = view.findViewById(R.id.bu_favorite)
    }
}