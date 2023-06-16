package com.example.caliscapstone.data.model.pengayaan

import java.io.Serializable
import java.util.ArrayList

data class ResponsePengayaan(
    val lessonId: String?= null,
    val lessonType: String?= null,
    val questions: ArrayList<Question>?= null
) : Serializable