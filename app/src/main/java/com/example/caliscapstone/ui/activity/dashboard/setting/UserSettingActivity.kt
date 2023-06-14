package com.example.caliscapstone.ui.activity.dashboard.setting

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.login.Children
import com.example.caliscapstone.data.model.login.RandomUuidValue
import com.example.caliscapstone.data.model.login.ResponseLogin
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeQuestionActivity
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSettingActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)

        val serverClientId = getString(R.string.web_client_id)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)

        if (account == null) {
            goSignOut()
        }

        getUserLogin()

    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getUserLogin() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_user)
        recyclerView.layoutManager = LinearLayoutManager(this@UserSettingActivity)
        var children: ResponseLogin
        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getLogin("Bearer $idToken")
                .enqueue(object : Callback<ResponseLogin> {
                    override fun onResponse(
                        call: Call<ResponseLogin>,
                        response: Response<ResponseLogin>
                    ) {
                        try {
                            val responseBody = response.body()!!
                            children = responseBody
                            var childrenValue: RandomUuidValue
                            Log.d("UserAccount", children.toString())
                            /*
                            children.children.forEach {
                                // entry ->  "${entry.key} : ${entry.value}"
                                    entry ->
                                childrenValue = entry.value
                                val adapter = UserAdapter(childrenValue, object : UserAdapter.OnAdapterListener {
                                    override fun onItemClicked(data: Lesson) {
                                        // Toast.makeText(applicationContext, data.lessonType, Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@HomeLessonActivity, HomeQuestionActivity::class.java)
                                            .putExtra("intent_id", data.lessonId)
                                            .putExtra("intent_level", data.lessonLevel.toString())
                                            .putExtra("intent_type", data.lessonType)
                                            .putExtra("intent_question", data.questions)
                                        )
                                        finish()
                                    }
                                })
                                Log.d("dataChildren", dataChildren)
                            }

                            val adapter = UserSettingAdapter(children, object : UserSettingAdapter.OnAdapterListener {
                            })
                            recyclerView.adapter = adapter
                            */
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }

}