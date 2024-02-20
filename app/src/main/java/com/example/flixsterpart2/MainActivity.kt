package com.example.flixsterpart2


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), PersonClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PersonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PersonAdapter(this)
        recyclerView.adapter = adapter

        fetchPopularPersons()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchPopularPersons() {
        GlobalScope.launch(Dispatchers.IO) {
            val apiKey = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
            val url = URL("https://api.themoviedb.org/3/person/popular?api_key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            try {
                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append('\n')
                }
                bufferedReader.close()
                val json = stringBuilder.toString()
                parsePersons(json)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching persons", e)
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun parsePersons(json: String) {
        val personsList = mutableListOf<Person>()
        val jsonObject = JSONObject(json)
        val resultsArray = jsonObject.getJSONArray("results")
        for (i in 0 until resultsArray.length()) {
            val personObject = resultsArray.getJSONObject(i)
            val name = personObject.getString("name")
            val profilePath = personObject.getString("profile_path")
            val imageUrl = "https://image.tmdb.org/t/p/w500/$profilePath"
            val id = personObject.getInt("id")
            val person = Person(name, imageUrl, id)
            personsList.add(person)
        }
        runOnUiThread {
            adapter.setPersons(personsList)
        }
    }

    override fun onPersonClick(person: Person) {
        // Create an intent to start the PersonDetailsActivity
        val intent = Intent(this, PersonDetailsActivity::class.java)

        // Pass the person's ID as an extra to the intent
        intent.putExtra("person_id", person.id)

        // Start the activity with the intent
        startActivity(intent)
    }
}