package com.sedra.goiptv.data

import com.sedra.goiptv.data.remote.ApiService
import com.sedra.goiptv.di.BaseApiService
import com.sedra.goiptv.di.IptvApiService
import com.sedra.goiptv.utils.*
import javax.inject.Inject


class BaseRepository @Inject constructor(
    @BaseApiService private val service: ApiService,
    ) {

    suspend fun login(code: String) =
        service.login(code)

    suspend fun getSubSections(id: Int) =
        service.getSubSections(id)


    suspend fun getItems(id: Int) =
        service.getItems(id)

    suspend fun getSections() =
        service.getSections()



}