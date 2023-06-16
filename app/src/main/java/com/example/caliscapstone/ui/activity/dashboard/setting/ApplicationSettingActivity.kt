package com.example.caliscapstone.ui.activity.dashboard.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.delete.ResponseDelete
import com.example.caliscapstone.data.model.login.RandomUuidValue
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
        setLoadingState(false)

        /* Google Sign In Verification */
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

        /* Intent Extra */
        val intentId = intent.getStringExtra("intent_uuid")
        val intentName = intent.getStringExtra("intent_name")
        val intentAge = intent.getStringExtra("intent_age")
        if (intentId != null) {
            Log.d("successDebugId", intentId)
        }
        if (intentName != null) {
            Log.d("successDebugName", intentName)
        }
        if (intentAge != null) {
            Log.d("successDebugAge", intentAge)
        }

        /* Property */
        gImage = findViewById(R.id.g_image)

        /* toolbar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        /* Update Form */
        val nameForm = findViewById<EditTextClear>(R.id.ed_name)
        val ageForm = findViewById<EditTextClear>(R.id.ed_age)
        val gUpdate = findViewById<Button>(R.id.g_update)

        gUpdate.setOnClickListener {
            val childName = nameForm.text.toString()
            val childAge = ageForm.text.toString().toInt()
            val updateChild = UpdateResponse(intentId, childName, childAge)
            getUpdateChild(updateChild)
        }

        /* Delete Account */
        val gDelete = findViewById<Button>(R.id.g_delete)
        gDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.dialogTitle)
            builder.setMessage(R.string.dialogMessage)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Yes") { _, _ ->

                val deleteChild = intentId?.let { it1 -> ResponseDelete(it1) }
                if (deleteChild != null) {
                    getUserDelete(deleteChild)
                }
                goSelectUser()
                Toast.makeText(applicationContext, "Profile berhasil di hapus", Toast.LENGTH_LONG)
                    .show()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        /* Google Account Data */
        getAccount()

        /* Sign Out */
        gSignOut = findViewById(R.id.g_sign_out)
        gSignOut.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.signout)
            builder.setMessage(R.string.areyousure)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Yes") { _, _ ->
                goSignOut()
                Toast.makeText(
                    applicationContext,
                    "Anda berhasil logout dari aplikasi",
                    Toast.LENGTH_LONG
                ).show()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        if (intentId != null) {
            getChildrenData(intentId )
        }

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

    private fun goSelectUser() {
        startActivity(Intent(this, UserSettingActivity::class.java))
        finish()
    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getUpdateChild(updateChild: UpdateResponse) {
        setLoadingState(true)

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
                when (response.code()) {
                    401 -> {
                        val builder = AlertDialog.Builder(this@ApplicationSettingActivity)
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
                            "Perbaharui profil gagal dilakukan silahkan coba lagi",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    413 -> {
                        setLoadingState(false)
                        error.postValue(getString(R.string.API_error_large_payload))
                    }

                    200 -> {
                        addResult.postValue(response.body())
                        Log.d("Update Account", response.body().toString())
                        setLoadingState(false)
                        val builder = AlertDialog.Builder(this@ApplicationSettingActivity)
                        builder.setTitle(R.string.update_title)
                        builder.setMessage(R.string.update_subtitle)
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                        builder.setPositiveButton("Ok") { dialog, _ ->
                            dialog.dismiss()
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                setLoadingState(false)
                Log.e(Constanta.TAG_AUTH, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    private fun getChildrenData(childId: String) {
        setLoadingState(true)

        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        val error = MutableLiveData("")

        val client = ApiConfig.getApiService().getChildren("Bearer $idToken", childId)
        client.enqueue(object : Callback<RandomUuidValue> {
            override fun onResponse(
                call: Call<RandomUuidValue>,
                response: Response<RandomUuidValue>
            ) {
                when (response.code()) {
                    401 -> {
                        val builder = AlertDialog.Builder(this@ApplicationSettingActivity)
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
                            "Gagal mendapatkan profile akun",
                            Toast.LENGTH_LONG
                        ).show()
                        setLoadingState(false)
                    }

                    413 -> {
                        setLoadingState(false)
                        error.postValue(getString(R.string.API_error_large_payload))
                    }

                    200 -> {
                        val nameForm = findViewById<EditTextClear>(R.id.ed_name)
                        val ageForm = findViewById<EditTextClear>(R.id.ed_age)
                        val responseBody = response.body()!!
                        val ageCalculation = 2023 - responseBody.yearOfBirth
                        val childName: Editable = SpannableStringBuilder(responseBody.childName)
                        val childAge: Editable = SpannableStringBuilder(ageCalculation.toString())
                        nameForm.text = childName
                        ageForm.text = childAge
                        Log.d("Update Account", responseBody.toString())
                        setLoadingState(false)
                    }
                }
            }

            override fun onFailure(call: Call<RandomUuidValue>, t: Throwable) {
                setLoadingState(false)
                Log.e(Constanta.TAG_AUTH, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    private fun getUserDelete(deleteChild: ResponseDelete) {
        setLoadingState(true)
        val error = MutableLiveData("")
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
                        when (response.code()) {
                            401 -> {
                                val builder = AlertDialog.Builder(this@ApplicationSettingActivity)
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
                                    "Gagal, menghapus profile gagal dilakukan",
                                    Toast.LENGTH_LONG
                                ).show()
                                setLoadingState(false)
                            }

                            413 -> {
                                setLoadingState(false)
                                error.postValue(getString(R.string.API_error_large_payload))
                            }

                            200 -> {
                                val responseBody = response.body()!!
                                Log.d("Delete Account", responseBody.toString())
                                setLoadingState(false)
                            }
                        }

                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<ResponseDelete>, t: Throwable) {
                        setLoadingState(false)
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }

    private fun getAccount() {
        setLoadingState(true)
        val gName = findViewById<TextView>(R.id.g_a_name)
        val gEmail = findViewById<TextView>(R.id.g_a_email)
        val gPhoto = findViewById<ImageView>(R.id.g_a_image)
        val error = MutableLiveData("")
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
                        when (response.code()) {
                            401 -> {
                                val builder = AlertDialog.Builder(this@ApplicationSettingActivity)
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
                                    "Gagal mendapatkan data akun google",
                                    Toast.LENGTH_LONG
                                ).show()
                                setLoadingState(false)
                            }

                            413 -> {
                                setLoadingState(false)
                                error.postValue(getString(R.string.API_error_large_payload))
                            }

                            200 -> {
                                gName.text = response.body()!!.name
                                gEmail.text = response.body()!!.user
                                Glide.with(this@ApplicationSettingActivity)
                                    .load(response.body()!!.picture)
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(gPhoto)
                                Log.d("UserName", gName.toString())
                                Log.d("User_Email", gEmail.toString())
                                setLoadingState(false)
                            }
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<Whoami>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                        setLoadingState(false)
                    }

                })
        }
    }

}