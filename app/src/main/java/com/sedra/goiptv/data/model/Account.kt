package com.sedra.goiptv.data.model

data class Account(
    val code_id: Int,
    val created_at: String,
    val ended_at: String,
    val id: Int,
    val password: String,
    val server: String,
    val started_at: String,
    val updated_at: String,
    val url: String,
    val username: String
)