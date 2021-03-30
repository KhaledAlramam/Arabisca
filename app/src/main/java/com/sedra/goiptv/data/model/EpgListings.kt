package com.sedra.goiptv.data.model

data class EpgListings(
        val channel_id: String,
        val description: String,
        val end: String,
        val epg_id: String,
        val id: String,
        val lang: String,
        val start: String,
        val start_timestamp: String,
        val stop_timestamp: String,
        val title: String
)