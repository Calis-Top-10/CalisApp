package com.example.caliscapstone.ui.activity.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.dummy.calculate.response.Item
import com.example.caliscapstone.ui.activity.dummy.calculate.response.Lesson
import com.example.caliscapstone.ui.adapter.calculate.CalculateTypeAdapter
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class ApplicationSettingActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var rvLessonsList: RecyclerView
    private val lessonsList: ArrayList<Lesson> = ArrayList()
    private val itemList: ArrayList<Item> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_setting)
        rvLessonsList = findViewById(R.id.rvLessonsList)
        rvLessonsList.setHasFixedSize(true)

        val serverClientId = getString(R.string.web_client_id)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)

        if (account==null) {
            goSignOut()
        }

        // Instance of lessons list using the data model class.

        try {
            // As we have JSON object, so we are getting the object
            //Here we are calling a Method which is returning the JSON object
            val obj = JSONObject(getJSONFromAssets()!!)

            // fetch JSONArray named lessons by using getJSONArray
            val lessonsArray = obj.getJSONArray("lessons")
            // Get the lessons data using for loop i.e. id, name, email and so on

            for (i in 0 until lessonsArray.length()) {
                // Create a JSONObject for fetching single Lesson's Data
                val lesson = lessonsArray.getJSONObject(i)
                // Fetch id store it in variable
                val id = lesson.getInt("id")
                val items = itemList
                // Now add all the variables to the data model class and the data model class to the array list.
                val lessonData =
                    Lesson(id, items)

                // add the details in the list
                lessonsList.add(lessonData)
            }
        } catch (e: JSONException) {
            //exception
            e.printStackTrace()
        }
        showRecyclerList()
    }
    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    /**
     * Method to load the JSON from the Assets file and return the object
     */
    private fun getJSONFromAssets(): String? {

        val json: String?
        val charset: Charset = Charsets.UTF_8
        try {
            val lessonsJSONFile = assets.open("soal_hitung.jsonc")
            val size = lessonsJSONFile.available()
            val buffer = ByteArray(size)
            lessonsJSONFile.read(buffer)
            lessonsJSONFile.close()
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
        val itemAdapter = CalculateTypeAdapter(this, lessonsList)
        // adapter instance is set to the recyclerview to inflate the items.
        rvLessonsList.adapter = itemAdapter
        itemAdapter.setOnItemClickCallback(object : CalculateTypeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Lesson) {
                val intentToDetail = Intent(this@ApplicationSettingActivity, HayoActivity::class.java)
                intentToDetail.putExtra("DATA", data)
                startActivity(intentToDetail)
            }
        })
    }
}