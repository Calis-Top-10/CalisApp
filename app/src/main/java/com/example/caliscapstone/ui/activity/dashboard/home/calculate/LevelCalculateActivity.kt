package com.example.caliscapstone.ui.activity.dashboard.home.calculate

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

class LevelCalculateActivity : AppCompatActivity() {
    private lateinit var rvLessonsList: RecyclerView
    private val itemList: ArrayList<Item> = ArrayList()
    private val answerList: List<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_calculate)
        rvLessonsList = findViewById(R.id.rvLessonsList)
        rvLessonsList.setHasFixedSize(true)

        val data = intent.getParcelableExtra<Lesson>("DATA")
        Log.d("Detail Data", data?.id.toString())

        // Instance of lessons list using the data model class.

        try {
            // As we have JSON object, so we are getting the object
            //Here we are calling a Method which is returning the JSON object
            val obj = JSONObject(getJSONFromAssets()!!)
            // fetch JSONArray named lessons by using getJSONArray
            val lessonsArray = obj.getJSONArray("lessons")
            // Get the lessons data using for loop i.e. id, name, email and so on
            print(lessonsArray)

            for (i in 0 until lessonsArray.length()) {
                // Create a JSONObject for fetching single Lesson's Data
                val lesson = lessonsArray.getJSONObject(i)
                // Fetch id store it in variable
                val id = lesson.getInt("id")
                val type = lesson.getString("type")
                val question = lesson.getString("question")
                val question_img = lesson.getString("question_img")
                val hint = lesson.getString("hint")
                val answer = answerList
                // Now add all the variables to the data model class and the data model class to the array list.
                val lessonDetails =
                    Item(id, type, question, question_img, hint, answer )

                Log.d("Detail Data", lessonDetails.toString())
                // add the details in the list
                itemList.add(lessonDetails)
            }
        } catch (e: JSONException) {
            //exception
            e.printStackTrace()
        }
        showRecyclerList()

    }

    private fun getJSONFromAssets(): String? {

        val json: String?
        val charset: Charset = Charsets.UTF_8
        try {
            val myLessonsJSONFile = assets.open("soal_hitung.jsonc")
            val size = myLessonsJSONFile.available()
            val buffer = ByteArray(size)
            myLessonsJSONFile.read(buffer)
            myLessonsJSONFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
    private fun showRecyclerList() {

        // Set the LayoutManager that this RecyclerView will use.
        rvLessonsList.layoutManager = LinearLayoutManager(this)
        // Adapter class is initialized and list is passed in the param.
        val itemAdapter = CalculateLevelAdapter(this, itemList)
        // adapter instance is set to the recyclerview to inflate the items.
        rvLessonsList.adapter = itemAdapter
    }
}