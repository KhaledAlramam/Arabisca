package com.sedra.goiptv.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sedra.goiptv.data.BaseRepository
import com.sedra.goiptv.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
        private val repository: BaseRepository
): ViewModel() {
    fun login(code: String, macAdd: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.login(code, macAdd)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }

    }
    fun getSetting() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getSettings()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }

    }
}