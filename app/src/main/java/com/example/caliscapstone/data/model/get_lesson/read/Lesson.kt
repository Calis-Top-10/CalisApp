package com.example.caliscapstone.data.model.get_lesson.read

import java.io.Serializable

data class Lesson(
    val lessonId: String,
    val lessonLevel: Int,
    val lessonType: String,
    val questions: ArrayList<Question>
) : Serializable