package com.example.caliscapstone.ui.activity.dashboard.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.login.Children
import com.example.caliscapstone.data.model.login.RandomUuidValue
import com.example.caliscapstone.data.model.login.ResponseLogin
import com.example.caliscapstone.ui.activity.adduser.AddUserActivity
import com.example.caliscapstone.ui.activity.dashboard.home.HomeActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeLessonAdapter
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeQuestionActivity
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import org.checkerframework.checker.units.qual.Length
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import kotlin.concurrent.schedule

class UserSettingActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)
        setLoadingState(false)

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

        val addUserContinue = findViewById<FrameLayout>(R.id.addButton)
        addUserContinue.setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
            finish()
        }

        onRefresh()

    }

    private fun setLoadingState(loading: Boolean) {
        when (loading) {
            true -> {
                findViewById<LottieAnimationView>(R.id.loading).visibility = View.VISIBLE
            }

            false -> {
                findViewById<LottieAnimationView>(R.id.loading).visibility = View.GONE
            }
        }
    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getUserLogin() {
        setLoadingState(true)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_user)
        recyclerView.layoutManager = GridLayoutManager(this@UserSettingActivity, 2)
        val error = MutableLiveData("")
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
                        when (response.code()) {
                            401 -> {
                                val builder = AlertDialog.Builder(this@UserSettingActivity)
                                builder.setTitle(R.string.signout)
                                builder.setMessage(R.string.API_error_header_token)
                                builder.setIcon(android.R.drawable.ic_dialog_alert)
                                builder.setPositiveButton("Ok") { _, _ ->
                                    goSignOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Anda berhasil logout dari aplikasi",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.setCancelable(false)
                                alertDialog.show()

                                setLoadingState(false)
                            }

                            413 -> {
                                setLoadingState(false)
                                error.postValue(getString(R.string.API_error_large_payload))
                            }

                            200 -> {
                                val responseBody = response.body()!!
                                children = responseBody
                                if (children.children.isNotEmpty()) {
                                    Log.d("UserAccount", children.toString())
                                    val adapter = UserAdapter(
                                        children.children,
                                        object : UserAdapter.OnAdapterListener {
                                            override fun onItemClicked(
                                                childClick: RandomUuidValue,
                                                uuid: String
                                            ) {
                                                startActivity(
                                                    Intent(
                                                        this@UserSettingActivity,
                                                        HomeActivity::class.java
                                                    )
                                                        .putExtra(
                                                            "intent_name",
                                                            childClick.childName
                                                        )
                                                        .putExtra(
                                                            "intent_age",
                                                            childClick.yearOfBirth.toString()
                                                        )
                                                        .putExtra("intent_uuid", uuid)
                                                )
                                                Toast.makeText(
                                                    this@UserSettingActivity,
                                                    "Selamat belajar ${childClick.childName}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                finish()
                                            }
                                        })
                                    setLoadingState(false)
                                    recyclerView.adapter = adapter
                                } else {
                                    goAddUser()
                                    setLoadingState(false)
                                }
                            }

                            else -> {
                                error.postValue("Error ${response.code()} : ${response.message()}")
                                setLoadingState(false)
                            }
                        }

                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        setLoadingState(false)
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }

    private fun goAddUser() {
        startActivity(Intent(this, AddUserActivity::class.java))
        finish()
    }

    private fun onRefresh() {
        findViewById<SwipeRefreshLayout>(R.id.swipeRefresh).setOnRefreshListener {
            Timer().schedule(2000) {
                findViewById<SwipeRefreshLayout>(R.id.swipeRefresh).isRefreshing = false
            }
        }
    }

}