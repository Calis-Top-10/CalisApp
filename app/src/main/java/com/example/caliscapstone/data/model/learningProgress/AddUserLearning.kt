package com.example.caliscapstone.data.model.learningProgress

data class AddUserLearning(
    val childId: String,
    val lessonId: String,
    val timestamp: String,
    val attempts: List<QuestionAttempt>
)
