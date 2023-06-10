package com.example.caliscapstone.data.api

import com.example.caliscapstone.data.model.add_children.AddChildren
import com.example.caliscapstone.data.model.get_lesson.read.Lesson
import com.example.caliscapstone.data.model.whoami.Whoami
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface Api {
    @GET("whoami")
    fun getWhoami(
        @Header("Authorization") token: String,
    ) : Call<Whoami>

    @GET("getLessonsByType?lessonType=baca")
    fun getLessons(
        @Header("Authorization") token: String,
    ) : Call<MutableList<Lesson>>

    @Headers("Content-Type: application/json")
    @POST("addChildren")
    fun doAddChildren(
        @Header("Authorization") token: String,
        @Body postModel: AddChildren
    ): Call<AddChildren>

}