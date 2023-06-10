package com.example.caliscapstone.ui.activity.dashboard.setting.dummy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.dashboard.home.calculate.response.Item
import com.example.caliscapstone.ui.activity.dashboard.home.calculate.response.Lesson
import com.example.caliscapstone.ui.adapter.calculate.CalculateLevelAdapter
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class HayoActivity : AppCompatActivity() {
    private lateinit var rvUsersList: RecyclerView
    private val usersItemt: ArrayList<Item> = ArrayList()
    private val answerList: List<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hayo)
        rvUsersList = findViewById(R.id.rvUsersList)
        rvUsersList.setHasFixedSize(true)

        val data = intent.getParcelableExtra<Lesson>("DATA")
        Log.d("Detail Data", data?.id.toString())
        Log.d("Detail Data", data?.items.toString())

        // Instance of users list using the data model class.

        try {
            // As we have JSON object, so we are getting the object
            //Here we are calling a Method which is returning the JSON object
            val obj = JSONObject(getJSONFromAssets()!!)
            // fetch JSONArray named users by using getJSONArray
            val usersArray = obj.getJSONArray("items")
            // Get the users data using for loop i.e. id, name, email and so on

            for (i in 0 until usersArray.length()) {
                // Create a JSONObject for fetching single User's Data
                val user = usersArray.getJSONObject(i)
                // Fetch id store it in variable
                val id = user.getInt("id")
                val type = user.getString("type")
                val question = user.getString("question")
                val question_img = user.getString("question_img")
                val hint = user.getString("hint")
                val answer = answerList
                // Now add all the variables to the data model class and the data model class to the array list.
                val userDetails =
                    Item(id, type, question, question_img, hint, answer )

                // add the details in the list
                usersItemt.add(userDetails)
            }
        } catch (e: JSONException) {
            //exception
            e.printStackTrace()
        }
        showRecyclerList()

    }

    private fun getJSONFromAssets(): String? {

        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val myUsersJSONFile = assets.open("soal_hitung.jsonc")
            val size = myUsersJSONFile.available()
            val buffer = ByteArray(size)
            myUsersJSONFile.read(buffer)
            myUsersJSONFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
    private fun showRecyclerList() {

        // Set the LayoutManager that this RecyclerView will use.
        rvUsersList.layoutManager = LinearLayoutManager(this)
        // Adapter class is initialized and list is passed in the param.
        val itemAdapter = CalculateLevelAdapter(this, usersItemt)
        // adapter instance is set to the recyclerview to inflate the items.
        rvUsersList.adapter = itemAdapter
    }
}