package com.angelstudio.newsapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse
import org.threeten.bp.LocalDate

@Dao
interface TopHeadlineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articles: List<Article>)

    @Query("select * from TopHeadline ")
    fun getTopHeadline(): LiveData<List<Article>>

    @Query("delete from TopHeadline")
    fun deleteOldTopHeadline()
}