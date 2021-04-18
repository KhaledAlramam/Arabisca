package com.sedra.goiptv.data.model

data class UserInfo(
    val active_cons: String?= null,
    val allowed_output_formats: List<String>?= null,
    val auth: Int?= null,
    val created_at: String?= null,
    val exp_date: String?= null,
    val is_trial: String?= null,
    val max_connections: String?= null,
    val password: String?= null,
    val status: String?= null,
    val username: String?= null
)