package com.example.cs414_final_project;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class EventDetailsAdapter(
    private val context: Context,
    private val eventDetailsList: MutableList<String>
) : BaseAdapter() {

    override fun getCount(): Int = eventDetailsList.size

    override fun getItem(position: Int): Any = eventDetailsList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.gemini_card_item, parent, false)
        val item = eventDetailsList[position]

        // Split the item string into title and description
        val (title, description) = item.split(": ", limit = 2)

        // Set the title and description text in the corresponding TextViews
        view.findViewById<TextView>(R.id.title_text_view).text = title.trim()
        view.findViewById<TextView>(R.id.description_text_view).text = description.trim()

        return view
    }
}