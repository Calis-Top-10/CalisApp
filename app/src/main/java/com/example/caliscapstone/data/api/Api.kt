package com.example.caliscapstone.data.api

import com.example.caliscapstone.data.model.add_children.AddChildren
import com.example.caliscapstone.data.model.delete.ResponseDelete
import com.example.caliscapstone.data.model.get_lesson.ResponseRead
import com.example.caliscapstone.data.model.login.Children
import com.example.caliscapstone.data.model.login.RandomUuidValue
import com.example.caliscapstone.data.model.login.ResponseLogin
import com.example.caliscapstone.data.model.pengayaan.ResponsePengayaan
import com.example.caliscapstone.data.model.report.UserReport
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
    @GET("getChildById")
    fun getChildren(
        @Header("Authorization") token: String,
        @Query("childId") childId: String
    ) : Call<RandomUuidValue>

    @GET("personalLesson")
    fun getpersonalLesson(
        @Header("Authorization") token: String,
        @Query("childId") childId: String
    ) : Call<ResponsePengayaan>

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
        @Body deleteChild: ResponseDelete
    ) : Call<ResponseDelete>

    @Headers("Content-Type: application/json")
    @POST("updateChild")
    fun getUpdateChild(
        @Header("Authorization") token: String,
        @Body updateChild: UpdateResponse
    ) : Call<UpdateResponse>
    @GET("userReport")
    fun getUserReport(
        @Header("Authorization") token: String,
        @Query("childId") childId: String
    ) : Call<UserReport>

}