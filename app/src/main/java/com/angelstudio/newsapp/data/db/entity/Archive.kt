package com.angelstudio.newsapp.data.db.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*

@Entity(tableName = "Archive",indices = [Index(value = ["title"], unique = true)])
data class Archive(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String?,
    var content: String?,
    val description: String?,
    val publishedAt: String?,
    @Embedded(prefix="source_")
    val source: Source,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)