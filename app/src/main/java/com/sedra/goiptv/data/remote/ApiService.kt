package com.sedra.goiptv.data.remote

import com.google.android.exoplayer2.extractor.ts.SectionReader
import com.sedra.goiptv.data.model.*
import com.sedra.goiptv.data.model.series_models.SeriesDetailsResponse
import retrofit2.http.*

interface ApiService {

    companion object{
        const val BASE_URL = "http://codespanel.com/"
    }

    @POST("/gotv/public/api/auth/login")
    @FormUrlEncoded
    suspend fun login(
            @Field("code") code: String,
            @Field("mac_address") macAdd: String
    ): LoginResponse

    @GET("/gotv/public/api/sections")
    suspend fun getSections(): SectionsResponse


    @GET("/gotv/public/api/subsections/section/{id}}")
    suspend fun getSubSections(
            @Path("id") id: Int
    ): SubSectionsResponse

    @GET("/gotv/public/api/items/subsection/{id}}")
    suspend fun getItems(
            @Path("id") id: Int
    ): ItemsResponse

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