package com.example.caliscapstone.ui.activity.adduser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.add_children.AddChildren
import com.example.caliscapstone.ui.activity.dashboard.home.HomeActivity
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

        val serverClientId = getString(R.string.web_client_id)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)
        val buttonContinue = findViewById<FrameLayout>(R.id.continueButton)
        val testingContinue = findViewById<FrameLayout>(R.id.testingButton)

        buttonContinue.setOnClickListener {
            val nameForm = findViewById<EditTextClear>(R.id.ed_name)
            val ageForm = findViewById<EditTextClear>(R.id.ed_age)

            val childName = nameForm.text.toString()
            val childAge = ageForm.text.toString().toInt()
            val postModel = AddChildren(childName, childAge)
            addData(postModel)
        }

        testingContinue.setOnClickListener {
            goSignIn()
        }
    }

    private fun addData(postModel: AddChildren) {

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        val addResult = MutableLiveData<AddChildren>()
        val error = MutableLiveData("")

        val client = ApiConfig.getApiService().doAddChildren("Bearer $idToken",postModel)
        client.enqueue(object : Callback<AddChildren> {
            override fun onResponse(call: Call<AddChildren>, response: Response<AddChildren>) {
                if (response.isSuccessful) {
                    addResult.postValue(response.body())
                    goSignIn()
                } else {
                    response.errorBody()?.let {
                        val errorResponse = JSONObject(it.string())
                        val errorMessages = errorResponse.getString("message")
                        error.postValue("REGISTER ERROR : $errorMessages")
                    }
                }
            }

            override fun onFailure(call: Call<AddChildren>, t: Throwable) {
                Log.e(Constanta.TAG_AUTH, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    private fun goSignIn() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}