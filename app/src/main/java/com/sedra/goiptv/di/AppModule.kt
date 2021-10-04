package com.sedra.goiptv.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.data.remote.ApiService
import com.sedra.goiptv.utils.PREF_PARENT_USER
import com.sedra.goiptv.utils.PREF_PORT
import com.sedra.goiptv.utils.PREF_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @BaseRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @IptvRetrofit
    @Provides
    fun provideTvRetrofit(preferences: SharedPreferences): Retrofit {
        val link =
            "http://${preferences.getString(PREF_URL, "")}:${preferences.getString(PREF_PORT, "")}/"
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(link)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @BaseApiService
    @Provides
    @Singleton
    fun provideApi(@BaseRetrofit retrofit: Retrofit): ApiService =
            retrofit.create(ApiService::class.java)

    @IptvApiService
    @Provides
    fun provideTvApi(@IptvRetrofit retrofit: Retrofit): ApiService =
            retrofit.create(ApiService::class.java)


    @Provides
    @Singleton
    fun providePrefs(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }


    @Provides
    fun getParent(sharedPreferences: SharedPreferences): UserInfo {
        val obj = sharedPreferences.getString(PREF_PARENT_USER, "defValue")
        return if (obj == "defValue") {
            UserInfo()
        } else {
            val gson = Gson()
            val storedHashMapString =
                    sharedPreferences.getString(PREF_PARENT_USER, "")
            if (storedHashMapString.isNullOrEmpty()) UserInfo()
            val type = object : TypeToken<UserInfo>() {}.type
            gson.fromJson(storedHashMapString, type)
        }
    }

}


//    @IptvRetrofit
//    @Provides
//    @Singleton
//    fun provideIpTvRetrofit(): Retrofit {
//        val okLogInterceptor = OkLogInterceptor.builder().build()
//        val okHttpBuilder = OkHttpClient.Builder()
//        okHttpBuilder.addInterceptor(okLogInterceptor)
//        val okHttpClient: OkHttpClient = okHttpBuilder.build()
//        return Retrofit.Builder()
//            .baseUrl(ApiService.BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//    }
//
//
//    @Provides
//    @Singleton
//    fun getParent(sharedPreferences: SharedPreferences): UserInfo{
//        val obj = sharedPreferences.getString(PREF_PARENT_USER, "defValue")
//        return if (obj == "defValue") {
//            UserInfo()
//        } else {
//            val gson = Gson()
//            val storedHashMapString =
//                sharedPreferences.getString(PREF_PARENT_USER, "")
//            if (storedHashMapString.isNullOrEmpty()) UserInfo()
//            val type = object : TypeToken<UserInfo>() {}.type
//            gson.fromJson(storedHashMapString, type)
//        }
//    }
//
//    @BaseApiService
//    @Provides
//    @Singleton
//    fun provideBaseApi(@BaseRetrofit retrofit: Retrofit): ApiService =
//        retrofit.create(ApiService::class.java)
//
//    @IptvApiService
//    @Provides
//    @Singleton
//    fun provideIPTVApi(@IptvRetrofit retrofit: Retrofit): ApiService =
//        retrofit.create(ApiService::class.java)


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IptvRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseApiService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IptvApiService