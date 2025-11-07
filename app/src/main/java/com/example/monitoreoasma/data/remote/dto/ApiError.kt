package com.example.monitoreoasma.data.remote.dto

data class ApiErrorBody(
    val code: String?,
    val message: String?
)

data class ApiError(
    val error: ApiErrorBody?
)