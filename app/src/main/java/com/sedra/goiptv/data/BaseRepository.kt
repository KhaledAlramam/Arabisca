package com.sedra.goiptv.data

import com.sedra.goiptv.data.remote.ApiService
import com.sedra.goiptv.di.BaseApiService
import javax.inject.Inject


class BaseRepository @Inject constructor(
    @BaseApiService private val service: ApiService,
    ) {

    suspend fun getAccounts(code: String, macAdd: String) =
        service.getAccounts(code, macAdd)

    suspend fun checkAccount(id: Int) =
        service.checkAccount(id)

    suspend fun getSubSections(id: Int) =
        service.getSubSections(id)

    suspend fun getSeriesFromSubSections(id: Int) =
        service.getSeriesFromSubSections(id)


    suspend fun getSeasonItems(id: Int) =
        service.getSeasonItems(id)


    suspend fun getSeriesSeasons(id: Int) =
            service.getSeriesSeasons(id)


    suspend fun getItems(id: Int) =
            service.getItems(id)

    suspend fun getSections() =
            service.getSections()


    suspend fun getSettings() =
        service.getSettings()



}