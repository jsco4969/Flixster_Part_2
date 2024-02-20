package com.example.flixsterpart2


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PersonAdapter(private val listener: PersonClickListener) : RecyclerView.Adapter<PersonViewHolder>() {

    private var persons = listOf<Person>()

    @SuppressLint("NotifyDataSetChanged")
    fun setPersons(persons: List<Person>) {
        this.persons = persons
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = persons[position]
        holder.bind(person)
    }

    override fun getItemCount(): Int {
        return persons.size
    }
}
