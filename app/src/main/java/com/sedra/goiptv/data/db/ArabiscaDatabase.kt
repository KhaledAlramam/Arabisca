package com.sedra.goiptv.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavouriteItem::class], version = 1, exportSchema = false)
abstract class ArabiscaDatabase : RoomDatabase() {

    abstract fun favouritesDao(): FavouritesDao

    companion object {
        @Volatile
        private var INSTANCE: ArabiscaDatabase? = null

        fun getDb(context: Context): ArabiscaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        ArabiscaDatabase::class.java,
                        "arabisca_db"
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}