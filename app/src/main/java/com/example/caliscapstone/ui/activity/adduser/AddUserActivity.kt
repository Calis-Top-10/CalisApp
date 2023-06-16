package com.example.caliscapstone.ui.activity.adduser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.LottieAnimationView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.add_children.AddChildren
import com.example.caliscapstone.ui.activity.dashboard.home.HomeActivity
import com.example.caliscapstone.ui.activity.dashboard.setting.UserSettingActivity
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.example.caliscapstone.ui.customview.EditTextClear
import com.example.caliscapstone.utils.Constanta
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddUserActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        setLoadingState(false)

        val serverClientId = getString(R.string.web_client_id)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)
        val buttonContinue = findViewById<FrameLayout>(R.id.continueButton)

        buttonContinue.setOnClickListener {
            val nameForm = findViewById<EditTextClear>(R.id.ed_name)
            val ageForm = findViewById<EditTextClear>(R.id.ed_age)

            val childName = nameForm.text.toString()
            val childAge = ageForm.text.toString().toInt()
            val postModel = AddChildren(childName, childAge)
            addData(postModel)
        }
    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun addData(postModel: AddChildren) {
        setLoadingState(true)

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        val addResult = MutableLiveData<AddChildren>()
        val error = MutableLiveData("")

        val client = ApiConfig.getApiService().doAddChildren("Bearer $idToken",postModel)
        client.enqueue(object : Callback<AddChildren> {
            override fun onResponse(call: Call<AddChildren>, response: Response<AddChildren>) {
                when (response.code()) {
                    401 -> {
                        val builder = AlertDialog.Builder(this@AddUserActivity)
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
                    400 -> {
                        Toast.makeText(
                            applicationContext,
                            "Membuat profil baru gagal dilakukan silahkan coba lagi",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    413 -> {
                        setLoadingState(false)
                        error.postValue(getString(R.string.API_error_large_payload))
                    }

                    200 -> {
                        addResult.postValue(response.body())
                        goSignIn()
                        Toast.makeText(applicationContext,"Profile berhasil di tambahkan", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<AddChildren>, t: Throwable) {
                setLoadingState(false)
                Log.e(Constanta.TAG_AUTH, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    private fun setLoadingState(loading: Boolean) {
        when(loading) {
            true -> {
                findViewById<LottieAnimationView>(R.id.loading).visibility = View.VISIBLE
            }
            false -> {
                findViewById<LottieAnimationView>(R.id.loading).visibility = View.GONE
            }
        }
    }

    private fun goSignIn() {
        val intent = Intent(this, UserSettingActivity::class.java)
        startActivity(intent)
        finish()
        setLoadingState(false)
    }
}