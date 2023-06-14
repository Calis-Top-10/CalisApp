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
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.delete.ResponseDelete
import com.example.caliscapstone.data.model.update.UpdateResponse
import com.example.caliscapstone.data.model.whoami.Whoami
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

class ApplicationSettingActivity : AppCompatActivity() {

    private lateinit var gImage: ImageView
    private lateinit var gSignOut: Button
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_setting)

        gImage = findViewById(R.id.g_image)
        gSignOut = findViewById(R.id.g_sign_out)

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

        gSignOut.setOnClickListener {
            goSignOut()
        }

        /* toolbar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val gUpdate = findViewById<Button>(R.id.g_update)
        gUpdate.setOnClickListener {
            val nameForm = findViewById<EditTextClear>(R.id.ed_name)
            val ageForm = findViewById<EditTextClear>(R.id.ed_age)

            val childId = "29b9c342-0c75-459a-8513-23443aec7283"
            val childName = nameForm.text.toString()
            val childAge = ageForm.text.toString().toInt()
            val updateChild = UpdateResponse(childId, childName, childAge)
            getUpdateChild(updateChild)
        }

        val gDelete = findViewById<Button>(R.id.g_delete)
        gDelete.setOnClickListener {

            val childId = "29b9c342-0c75-459a-8513-23443aec7283"
            val deleteChild = ResponseDelete(childId)
            getUserDelete(deleteChild)
        }
        getAccount()

    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getUpdateChild(updateChild: UpdateResponse) {

        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        val addResult = MutableLiveData<UpdateResponse>()
        val error = MutableLiveData("")

        val client = ApiConfig.getApiService().getUpdateChild("Bearer $idToken", updateChild)
        client.enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(
                call: Call<UpdateResponse>,
                response: Response<UpdateResponse>
            ) {
                if (response.isSuccessful) {
                    addResult.postValue(response.body())
                    Log.d("Update Account", response.body().toString())
                } else {
                    response.errorBody()?.let {
                        val errorResponse = JSONObject(it.string())
                        val errorMessages = errorResponse.getString("message")
                        error.postValue("REGISTER ERROR : $errorMessages")
                    }
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                Log.e(Constanta.TAG_AUTH, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    private fun getUserDelete(deleteChild: ResponseDelete) {
        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getDeleteChild("Bearer $idToken", deleteChild)
                .enqueue(object : Callback<ResponseDelete> {
                    override fun onResponse(
                        call: Call<ResponseDelete>,
                        response: Response<ResponseDelete>
                    ) {
                        try {
                            val responseBody = response.body()!!
                            Log.d("Delete Account", responseBody.toString())
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<ResponseDelete>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }

    private fun getAccount() {
        val gName = findViewById<TextView>(R.id.g_a_name)
        val gEmail = findViewById<TextView>(R.id.g_a_email)
        val gPhoto = findViewById<ImageView>(R.id.g_a_image)
        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getWhoami("Bearer $idToken")
                .enqueue(object : Callback<Whoami> {
                    override fun onResponse(
                        call: Call<Whoami>,
                        response: Response<Whoami>
                    ) {
                        try {
                            gName.text = response.body()!!.name
                            gEmail.text = response.body()!!.user
                            Glide.with(this@ApplicationSettingActivity)
                                .load(response.body()!!.picture)
                                .centerCrop()
                                .dontAnimate()
                                .into(gPhoto)
                            Log.d("UserName", gName.toString())
                            Log.d("User_Email", gEmail.toString())
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<Whoami>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }

}