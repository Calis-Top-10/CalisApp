package com.example.caliscapstone.data.model.login

data class ResponseLogin(
    val children: Map<String, RandomUuidValue>,
    val createdAt: String,
    val email: String
)