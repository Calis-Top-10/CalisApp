package com.example.caliscapstone.data.model.get_lesson

import java.io.Serializable

data class QuestionDetails(
    val answer: List<String> ?=null,
    val feedback_img: String?=null,
    val question: String ?=null,
    val tags: List<String>?=null,
    val hint: String?=null,
    val question_img: String?=null
) : Serializable