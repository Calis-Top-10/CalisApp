package com.example.caliscapstone.ui.activity.dummy.read.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val answer: List<String>,
    val feedback_img: String,
    val id: Int,
    val question: String,
    val type: String
): Parcelable