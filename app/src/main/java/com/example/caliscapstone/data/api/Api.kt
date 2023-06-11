package com.example.caliscapstone.data.api

import com.example.caliscapstone.data.model.add_children.AddChildren
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.get_lesson.Question
import com.example.caliscapstone.data.model.get_lesson.ResponseRead
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

    @GET("getLesson?lessonId=04fb20c5-6e65-474f-805c-cb5d0e104da8")
    fun getQuestion1(
        @Header("Authorization") token: String,
    ) : Call<Lesson>

    @GET("getQuestion?questionId=4dd53e83-fdeb-4d2b-ab38-f10520022e88")
    fun getQuestion2(
        @Header("Authorization") token: String,
    ) : Call<Question>

    @Headers("Content-Type: application/json")
    @POST("addChildren")
    fun doAddChildren(
        @Header("Authorization") token: String,
        @Body postModel: AddChildren
    ): Call<AddChildren>

}