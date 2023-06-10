package com.example.caliscapstone.data.model.get_lesson.read

data class Lesson(
    val lessonId: String,
    val lessonLevel: Int,
    val lessonType: String,
    val questions: List<Question>
)