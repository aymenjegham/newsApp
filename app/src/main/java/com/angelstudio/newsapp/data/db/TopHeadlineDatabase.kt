package com.angelstudio.newsapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.angelstudio.newsapp.data.db.entity.Archive
import com.angelstudio.newsapp.data.db.entity.Article


@Database(entities = [Article::class, Archive::class], version=3, exportSchema = false)
abstract class TopHeadlineDatabase : RoomDatabase() {

    abstract fun topHeadlineDao():TopHeadlineDao

    companion object{
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Archive ADD COLUMN isRead INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Archive ADD COLUMN savedAt INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "UPDATE Archive SET savedAt = (strftime('%s', 'now') * 1000) WHERE savedAt = 0"
                )
            }
        }

        @Volatile private var instance :TopHeadlineDatabase? =null
        private val LOCK =Any()

        operator fun invoke(context : Context) = instance?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                TopHeadlineDatabase::class.java, "topheadline.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
    }
}