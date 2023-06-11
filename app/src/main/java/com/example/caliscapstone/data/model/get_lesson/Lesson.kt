package com.example.caliscapstone.data.model.get_lesson

import java.util.ArrayList

data class Lesson(
    val lessonId: String,
    val lessonLevel: Int,
    val lessonType: String,
    val questions: ArrayList<Question>
)