package com.sedra.goiptv.data.model

data class GetAccountsResponse(
        val data: List<Account>,
        val message: String,
        val status: Int
)