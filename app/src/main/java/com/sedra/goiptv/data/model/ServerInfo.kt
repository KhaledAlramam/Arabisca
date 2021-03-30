package com.sedra.goiptv.data.model

data class ServerInfo(
    val https_port: String,
    val port: String,
    val process: Boolean,
    val rtmp_port: String,
    val server_protocol: String,
    val time_now: String,
    val timestamp_now: Int,
    val timezone: String,
    val url: String
)