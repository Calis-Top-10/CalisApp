package com.example.caliscapstone.api

import com.example.caliscapstone.api.model.Whoami
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface Api {
    @GET("whoami")
    fun getWhoami(
        @Header("Authorization") token: String,
    ) : Call<List<Whoami>>
}