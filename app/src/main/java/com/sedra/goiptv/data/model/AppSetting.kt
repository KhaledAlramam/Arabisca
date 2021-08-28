package com.sedra.goiptv.data.model

data class AppSetting(
    val setting: SettingFromServer,
    val apk: LatestApkDetails
)
data class SettingFromServer(
    val created_at: String,
    val id: Int,
    val image: String,
    val long_bar: String,
    val updated_at: String

)
data class LatestApkDetails(
    val name: String,
    val link: String,
    val version: Int,
    val id: Int,
)