package com.example.caliscapstone.ui.activity.dashboard.home.calculate.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val id: Int,
    val type: String,
    val hint: String,
    val question: String,
    val question_img: String,
    val answer: List<String>
): Parcelable