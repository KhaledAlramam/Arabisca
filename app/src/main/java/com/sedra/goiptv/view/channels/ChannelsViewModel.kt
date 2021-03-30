package com.sedra.goiptv.view.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sedra.goiptv.utils.Resource
import com.sedra.goiptv.data.DataRepository
import com.sedra.goiptv.data.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val repository: DataRepository,
    private val userInfo: UserInfo
) : ViewModel() {

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

    fun getEpg(streamId: Int, limit: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getEpg(userInfo.username, userInfo.password, streamId, limit)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.localizedMessage ?: "حدث خطأ ما"))
        }
    }


}