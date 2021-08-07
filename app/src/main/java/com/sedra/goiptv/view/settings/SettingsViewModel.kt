package com.sedra.goiptv.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sedra.goiptv.data.DataRepository
import com.sedra.goiptv.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: DataRepository
): ViewModel() {

    fun getSections() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getSections()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }

    }

    fun getVersions() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getVersions()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }

    }
}