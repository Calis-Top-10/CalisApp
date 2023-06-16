package com.example.caliscapstone.data.model.pengayaan

import java.io.Serializable

data class QuestionDetails(
    val answer: List<String>,
    val feedback_img: String,
    val question: String
) : Serializable