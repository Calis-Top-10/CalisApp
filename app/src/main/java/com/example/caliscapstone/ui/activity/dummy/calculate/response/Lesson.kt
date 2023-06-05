package com.example.caliscapstone.ui.activity.dummy.calculate.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson(
    val id: Int,
    val items: List<Item>
): Parcelable