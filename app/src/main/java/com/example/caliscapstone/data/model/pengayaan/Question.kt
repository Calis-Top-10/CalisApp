package com.example.caliscapstone.data.model.pengayaan

import java.io.Serializable

data class Question(
    val questionDetails: QuestionDetails,
    val questionId: String,
    val questionType: String,
    val tags: List<String>
) : Serializable