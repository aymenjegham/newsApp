package com.angelstudio.newsapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.angelstudio.newsapp.data.db.entity.Archive
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse


@Database(entities = [Article::class, Archive::class], version=1, exportSchema = false
)
abstract class TopHeadlineDatabase : RoomDatabase() {

    abstract fun topHeadlineDao():TopHeadlineDao

    companion object{
        @Volatile private var instance :TopHeadlineDatabase? =null
        private val LOCK =Any()

        operator fun invoke(context : Context) = instance?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                TopHeadlineDatabase::class.java, "topheadline.db")
                .build()

    }
}