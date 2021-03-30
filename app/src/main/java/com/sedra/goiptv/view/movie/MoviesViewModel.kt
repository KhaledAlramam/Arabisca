package com.sedra.goiptv.view.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sedra.goiptv.data.DataRepository
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: DataRepository,
    private val userInfo: UserInfo
): ViewModel() {

    fun getMovieDetails(movieId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getMovieDetails(userInfo.username, userInfo.password, movieId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }
}