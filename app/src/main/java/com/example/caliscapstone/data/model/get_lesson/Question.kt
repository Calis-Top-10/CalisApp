package com.example.caliscapstone.data.model.get_lesson

import java.io.Serializable

data class Question(
    val questionDetails: QuestionDetails,
    val questionId: String,
    val questionType: String,
    val tags: List<String>
) : Serializable