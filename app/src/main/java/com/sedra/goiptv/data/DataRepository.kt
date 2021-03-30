package com.sedra.goiptv.data

import com.sedra.goiptv.data.remote.ApiService
import com.sedra.goiptv.di.BaseApiService
import com.sedra.goiptv.di.IptvApiService
import com.sedra.goiptv.utils.*
import javax.inject.Inject


class DataRepository @Inject constructor(
    @BaseApiService private val service: ApiService,
    @IptvApiService private val iptvApiService: ApiService
    ) {

    suspend fun login(code: String) =
        service.login(code)

    suspend fun getSections() =
        service.getSections()


    suspend fun getAllMovies(userName: String?, password: String?) =
        iptvApiService.getMovies(userName, password, GET_MOVIES_ACTION)

    suspend fun getMovieDetails(userName: String?, password: String?, movieId: Int) =
        iptvApiService.getMovieDetails(userName, password, GET_MOVIE_DETAILS_ACTION, movieId)

    suspend fun getSeriesDetails(userName: String, password: String, seriesId: Int) =
        iptvApiService.getSeriesDetails(userName, password, GET_SERIES_DETAILS_ACTION, seriesId)

    suspend fun getMoviesCategories(userName: String?, password: String?) =
        iptvApiService.getSelectedCategories(userName, password, GET_MOVIES_CATEGORIES_ACTION)

    suspend fun getAllSeries(userName: String?, password: String?) =
        iptvApiService.getSeries(userName, password, GET_SERIES_ACTION)

    suspend fun getSeriesCategories(userName: String?, password: String?) =
        iptvApiService.getSelectedCategories(userName, password, GET_SERIES_CATEGORIES_ACTION)

    suspend fun getAllChannels(userName: String?, password: String?) =
        iptvApiService.getLiveStreams(userName, password, GET_LIVE_STREAMS_ACTION)

    suspend fun getChannelsCategories(userName: String?, password: String?) =
        iptvApiService.getSelectedCategories(userName, password, GET_LIVE_CATEGORIES_ACTION)

    suspend fun getEpg(userName: String?, password: String?, streamId: Int, limit: Int) =
        iptvApiService.getEpg(userName, password, GET_EPG_ACTION, streamId, limit)


//    companion object {
//        var loginDatabase: CobraDatabase? = null
//
//        private fun initializeDB(context: Context): CobraDatabase {
//            return CobraDatabase.getDb(context)
//        }
//
//        fun getAllFavourites(context: Context): Flow<List<FavouriteItem>> {
//            loginDatabase = initializeDB(context)
//            return loginDatabase!!.favouritesDao().getAllFavourites()
//        }
//
//        suspend fun addFavourite(context: Context, item: FavouriteItem) {
//            loginDatabase = initializeDB(context)
//            return loginDatabase!!.favouritesDao().addFavourite(item)
//        }
//
//        suspend fun removeFavourite(context: Context, item: FavouriteItem) {
//            loginDatabase = initializeDB(context)
//            return loginDatabase!!.favouritesDao().removeFavourite(item)
//        }
//
//    }


}