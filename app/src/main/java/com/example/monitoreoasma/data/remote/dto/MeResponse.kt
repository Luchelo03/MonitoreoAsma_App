package com.example.monitoreoasma.data.remote.dto

data class MeUser(
    val user_id: String
)

data class MeChild(
    val id: String,
    val name: String,
    val birthdate: String? = null,
    val sex: String? = null,
    val diagnosed_asthma: Boolean? = null,
    val diagnosis_date: String? = null,
    val started_app_at: String? = null,
    val created_at: String? = null
)

data class MeResponse(
    val user: MeUser,
    val children: List<MeChild>
)
