package com.example.caliscapstone.data.model.report

data class UserReport(
    val childId: String,
    val email: String,
    val learningProgress: LearningProgress,
    val tag: List<Any>
)