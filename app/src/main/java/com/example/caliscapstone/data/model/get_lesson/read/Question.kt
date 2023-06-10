package com.example.caliscapstone.data.model.get_lesson.read

import java.io.Serializable

data class Question(
    val additionalProp1: String,
    val additionalProp2: String,
    val additionalProp3: String,
    val answer: List<String>,
    val question: String,
    val type: String
)