package com.sedra.goiptv.view.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sedra.goiptv.data.DataRepository
import com.sedra.goiptv.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(private val repository: DataRepository
    ) : ViewModel() {


    fun getAllSeries(userName: String, password: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getAllSeries(userName, password)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }

    fun getSeriesCategories(userName: String, password: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getSeriesCategories(userName, password)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }

    fun getSeriesDetails(userName: String, password: String, seriesId: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = repository.getSeriesDetails(
                            userName,
                            password,
                            seriesId
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(
                    Resource.error(
                        data = null,
                        message = exception.localizedMessage ?: "حدث خطأ ما"
                    )
                )
            }
        }

}