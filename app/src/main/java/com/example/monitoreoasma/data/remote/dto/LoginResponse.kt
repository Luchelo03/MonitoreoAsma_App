package com.example.monitoreoasma.data.remote.dto

data class LoginUser(
    val user_id: String,
    val email: String,
    val name: String,
    val role: String
)

data class LoginResponse(
    val access_token: String,
    val expires_in: Long,
    val token_type: String,
    val user: LoginUser
)