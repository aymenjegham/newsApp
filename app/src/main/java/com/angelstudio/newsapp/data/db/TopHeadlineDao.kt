package com.angelstudio.newsapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.angelstudio.newsapp.data.db.entity.Archive
import com.angelstudio.newsapp.data.db.entity.Article

@Dao
interface TopHeadlineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articles: List<Article>)

    @Query("select * from TopHeadline ")
    fun getTopHeadline(): LiveData<List<Article>>

    @Query("delete from TopHeadline")
    fun deleteOldTopHeadline()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun archive(archive: Archive)

    @Delete
    fun desarchive(archive: Archive)

    @Query("select * from Archive ")
    fun getArchive(): LiveData<List<Article>>

    @Query("DELETE FROM Archive")
    fun deleteAll()
}