package com.example.flixsterpart2


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PersonViewHolder(itemView: View, private val listener: PersonClickListener) : RecyclerView.ViewHolder(itemView) {

    fun bind(person: Person) {
        itemView.findViewById<TextView>(R.id.nameTextView).text = person.name
        Glide.with(itemView)
            .load(person.imageUrl)
            .apply(RequestOptions().placeholder(R.drawable.placeholder))
            .into(itemView.findViewById(R.id.profileImageView))

        itemView.setOnClickListener {
            listener.onPersonClick(person)
        }
    }
}

