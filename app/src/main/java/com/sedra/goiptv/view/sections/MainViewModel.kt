package com.sedra.goiptv.view.sections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sedra.goiptv.data.DataRepository
import com.sedra.goiptv.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
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
}