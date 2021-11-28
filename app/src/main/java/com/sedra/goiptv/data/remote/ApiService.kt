package com.sedra.goiptv.data.remote

import com.sedra.goiptv.data.model.*
import com.sedra.goiptv.data.model.series_models.SeriesDetailsResponse
import retrofit2.http.*

interface ApiService {

    companion object{
        const val BASE_URL = "http://mahmoude28.sg-host.com/"
    }

    @POST("/arabisk/public/api/auth/login")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun getAccounts(
        @Field("code") code: String,
        @Field("mac_address") macAdd: String
    ): GetAccountsResponse

    @GET("/arabisk/public/api/sections")
    suspend fun getSections(): SectionsResponse


    @GET("/arabisk/public/api/check-client/{id}")
    suspend fun checkAccount(
        @Path("id") id: Int
    ): ClientCheckResponse

    @GET("/arabisk/public/api/versions")
    suspend fun getVersions(): VersionsResponse


    @GET("/arabisk/public/api/settings")
    suspend fun getSettings(): SettingsResponse


    @GET("/arabisk/public/api/subsections/section/{id}}")
    suspend fun getSubSections(
            @Path("id") id: Int
    ): SubSectionsResponse


    @GET("/arabisk/public/api/series/subsection/{id}}")
    suspend fun getSeriesFromSubSections(
            @Path("id") id: Int
    ): ItemsResponse


    @GET("/arabisk/public/api/items/season/{id}}")
    suspend fun getSeasonItems(
            @Path("id") id: Int
    ): ItemsResponse


    @GET("/arabisk/public/api/seasons/series/{id}}")
    suspend fun getSeriesSeasons(
            @Path("id") id: Int
    ): ItemsResponse

    @GET("/arabisk/public/api/items/subsection/{id}}")
    suspend fun getItems(
            @Path("id") id: Int
    ): ItemsResponse

    @POST("/player_api.php")
    @FormUrlEncoded
    suspend fun login(
            @Field("username") userName: String,
            @Field("password") password: String
    ): LoginResponse

    @POST("/player_api.php")
    @FormUrlEncoded
    suspend fun getSeries(
            @Field("username") userName: String?,
            @Field("password") password: String?,
            @Field("action") action: String
    ): List<Series>

    @POST("/player_api.php")
    @FormUrlEncoded
    suspend fun getMovies(
            @Field("username") userName: String?,
            @Field("password") password: String?,
            @Field("action") action: String
    ): List<Movie>

    @POST("/player_api.php")
    @FormUrlEncoded
    suspend fun getSelectedCategories(
            @Field("username") userName: String?,
            @Field("password") password: String?,
            @Field("action") action: String
    ): List<Category>

    @POST("/player_api.php")
    @FormUrlEncoded
    suspend fun getMovieDetails(
        @Field("username") userName: String?,
        @Field("password") password: String?,
        @Field("action") action: String,
        @Field("vod_id") movieId: Int
    ): MovieDetailsResponse

    @POST("/player_api.php")
    @FormUrlEncoded
    suspend fun getLiveStreams(
        @Field("username") userName: String?,
        @Field("password") password: String?,
        @Field("action") action: String
    ): List<LiveStream>

    @POST("/player_api.php")
    @FormUrlEncoded
    suspend fun getSeriesDetails(
            @Field("username") userName: String?,
            @Field("password") password: String?,
            @Field("action") action: String,
            @Field("series_id") seriesId: Int
    ): SeriesDetailsResponse

    @POST("/player_api.php")
    @FormUrlEncoded
    suspend fun getEpg(
            @Field("username") userName: String?,
            @Field("password") password: String?,
            @Field("action") action: String,
            @Field("stream_id") streamId: Int,
            @Field("limit") limit: Int
    ): EPGResponse


}