package com.sedra.goiptv.view

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sedra.goiptv.data.DataRepository
import com.sedra.goiptv.data.db.FavouriteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor() : ViewModel() {


    fun allFavourites(context: Context?): LiveData<List<FavouriteItem>> =
        DataRepository.getAllFavourites(context!!).asLiveData()

    fun addFavourite(context: Context, item: FavouriteItem) = viewModelScope.launch {
        DataRepository.addFavourite(context, item)
    }

    fun removeFavourite(context: Context?, item: FavouriteItem) =
        viewModelScope.launch(Dispatchers.IO) {
            if (context != null)
                DataRepository.removeFavourite(context, item)
        }

}