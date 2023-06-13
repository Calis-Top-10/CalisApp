package com.example.caliscapstone.data.api

import com.example.caliscapstone.data.model.add_children.AddChildren
import com.example.caliscapstone.data.model.delete.ResponseDelete
import com.example.caliscapstone.data.model.get_lesson.ResponseRead
import com.example.caliscapstone.data.model.login.Children
import com.example.caliscapstone.data.model.login.ResponseLogin
import com.example.caliscapstone.data.model.update.UpdateResponse
import com.example.caliscapstone.data.model.whoami.Whoami
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


interface Api {
    @GET("whoami")
    fun getWhoami(
        @Header("Authorization") token: String,
    ) : Call<Whoami>

    @GET("getLessonsByType")
    fun getLessons(
        @Header("Authorization") token: String,
        @Query("lessonType") type: String
    ) : Call<ResponseRead>

    @Headers("Content-Type: application/json")
    @POST("addChildren")
    fun doAddChildren(
        @Header("Authorization") token: String,
        @Body postModel: AddChildren
    ): Call<AddChildren>

    @GET("login")
    fun getLogin(
        @Header("Authorization") token: String,
    ) : Call<ResponseLogin>
    @POST("deleteChild")
    fun getDeleteChild(
        @Header("Authorization") token: String,
    ) : Call<ResponseDelete>

    @Headers("Content-Type: application/json")
    @POST("updateChild")
    fun getUpdateChild(
        @Header("Authorization") token: String,
        @Body postModel: UpdateResponse
    ) : Call<UpdateResponse>

}