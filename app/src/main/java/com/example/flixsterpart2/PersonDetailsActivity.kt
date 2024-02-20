package com.example.flixsterpart2

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PersonDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_details)

        val personId = intent.getIntExtra("person_id", -1)
        if (personId != -1) {
            fetchPersonDetails(personId)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchPersonDetails(personId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val apiKey = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
            val url = URL("https://api.themoviedb.org/3/person/$personId?api_key=$apiKey")
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
                parsePersonDetails(json)
            } catch (e: Exception) {
                Log.e("PersonDetailsActivity", "Error fetching person details", e)
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun parsePersonDetails(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val name = jsonObject.optString("name", "Unknown")
            val knownForArray = jsonObject.optJSONArray("known_for")

            if (knownForArray != null && knownForArray.length() > 0) {
                val knownFor = knownForArray.getJSONObject(0)
                val title = knownFor.optString("title", "Unknown")
                val overview = knownFor.optString("overview", "No overview available")
                val posterPath = knownFor.optString("poster_path")

                if (!posterPath.isNullOrEmpty()) {
                    val posterUrl = "https://image.tmdb.org/t/p/w500/$posterPath"
                    runOnUiThread {
                        findViewById<TextView>(R.id.personNameTextView).text = name
                        findViewById<TextView>(R.id.knownForTextView).text = title
                        findViewById<TextView>(R.id.overviewTextView).text = overview

                        Glide.with(this@PersonDetailsActivity)
                            .load(posterUrl)
                            .apply(RequestOptions().placeholder(R.drawable.placeholder))
                            .into(findViewById(R.id.posterImageView))
                    }
                } else {
                    Log.e("PersonDetailsActivity", "Poster path is empty or null")
                }
            } else {
                Log.e("PersonDetailsActivity", "No known_for data available")
            }
        } catch (e: JSONException) {
            Log.e("PersonDetailsActivity", "Error parsing JSON", e)
        }
    }
}

