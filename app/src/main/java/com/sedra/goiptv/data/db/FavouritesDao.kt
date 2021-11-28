package com.sedra.goiptv.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {

    @Query("SELECT * FROM arabisca_fav")
    fun getAllFavourites(): Flow<List<FavouriteItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavourite(item: FavouriteItem)

    @Delete
    suspend fun removeFavourite(item: FavouriteItem)
}