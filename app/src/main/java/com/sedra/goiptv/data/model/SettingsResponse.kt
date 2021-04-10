package com.sedra.goiptv.data.model

data class SettingsResponse(
    val data: AppSetting,
    val message: String,
    val status: Int
)