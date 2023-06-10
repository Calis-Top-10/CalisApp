package com.example.caliscapstone.ui.activity.dashboard.report

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.read.Lesson
import com.example.caliscapstone.ui.activity.dashboard.home.read.dummy.ReadLessonAdapter
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var rvLessonList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        val serverClientId = getString(R.string.web_client_id)
        rvLessonList = findViewById(R.id.rv_lesson)
        rvLessonList.setHasFixedSize(true)

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

        testToken()

    }
    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    /*

    private fun testApiCalling() {

        val recyclerView = findViewById<RecyclerView>(R.id.rv_lesson)

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getLessons("Bearer $idToken")
                .enqueue(object : Callback<MutableList<Lesson>> {
                    override fun onResponse(
                        call: Call<MutableList<Lesson>>,
                        response: Response<MutableList<Lesson>>
                    ) {
                        if (response.isSuccessful) {
                            Log.e("Success", response.body().toString())
                            recyclerView.apply {
                                layoutManager = LinearLayoutManager( this@ReportActivity)
                                adapter = ReadLessonAdapter(response.body()!!)
                            }
                        }
                    }

                    override fun onFailure(call: Call<MutableList<Lesson>>, t: Throwable) {
                        Log.e("error", t.message.toString())
                    }

                })
        }
    }
    */

    private fun testToken() {
        val serverClientId = getString(R.string.web_client_id)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(baseContext)
        val idToken = account?.idToken
        val msg = "idToken: $idToken"
        Log.d("one tap", msg)
    }
}

/*

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.adapter.UserAdapter
import com.example.caliscapstone.data.model.testing.UserModelClass
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

class ReportActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var rvUsersList: RecyclerView
    private val usersList: ArrayList<UserModelClass> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        rvUsersList = findViewById(R.id.rvUsersList)
        rvUsersList.setHasFixedSize(true)

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

        // Instance of users list using the data model class.

        try {
            // As we have JSON object, so we are getting the object
            //Here we are calling a Method which is returning the JSON object
            val obj = JSONObject(getJSONFromAssets()!!)
            // fetch JSONArray named users by using getJSONArray
            val usersArray = obj.getJSONArray("users")
            // Get the users data using for loop i.e. id, name, email and so on

            for (i in 0 until usersArray.length()) {
                // Create a JSONObject for fetching single User's Data
                val user = usersArray.getJSONObject(i)
                // Fetch id store it in variable
                val id = user.getInt("id")
                val name = user.getString("name")
                val email = user.getString("email")
                val gender = user.getString("gender")
                val weight = user.getDouble("weight")
                val height = user.getInt("height")

                // create a object for getting phone numbers data from JSONObject
                val phone = user.getJSONObject("phone")
                // fetch mobile number
                val mobile = phone.getString("mobile")
                // fetch office number
                val office = phone.getString("office")

                // Now add all the variables to the data model class and the data model class to the array list.
                val userDetails =
                    UserModelClass(id, name, email, gender, weight, height, mobile, office)

                // add the details in the list
                usersList.add(userDetails)
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

        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val myUsersJSONFile = assets.open("Users.json")
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
        val itemAdapter = UserAdapter(this, usersList)
        // adapter instance is set to the recyclerview to inflate the items.
        rvUsersList.adapter = itemAdapter
    }
}

 */