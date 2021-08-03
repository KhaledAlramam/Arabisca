package com.sedra.goiptv.view.department

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sedra.goiptv.data.DataRepository
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: DataRepository,
    private val userInfo: UserInfo
): ViewModel() {

    fun getAllMovies() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getAllMovies(userInfo.username, userInfo.password)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }

    fun getMoviesCategories() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            Log.e("TAG",  userInfo.toString())
            emit(Resource.success(data = repository.getMoviesCategories(userInfo.username, userInfo.password)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }

    fun getAllSeries() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getAllSeries(userInfo.username, userInfo.password)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }

    fun getSeriesCategories() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getSeriesCategories(userInfo.username, userInfo.password)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }

    fun getAllChannels() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getAllChannels(userInfo.username, userInfo.password)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }

    fun getChannelsCategories() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getChannelsCategories(userInfo.username, userInfo.password)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }

}