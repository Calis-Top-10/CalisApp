package com.example.caliscapstone.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Whoami (
//    @SerializedName("postId")
//    @Expose
//    var post_id: Int?=null,
//
//    @SerializedName("id")
//    @Expose
//    var id: Int?=null,
//
//    @SerializedName("name")
//    @Expose
//    var name: String?=null,
//
//    @SerializedName("email")
//    @Expose
//    var email: String?=null,
//
//    @SerializedName("body")
//    @Expose
//    var body: String?=null,

    @SerializedName("error")
    @Expose
    var error: String?=null,
)
