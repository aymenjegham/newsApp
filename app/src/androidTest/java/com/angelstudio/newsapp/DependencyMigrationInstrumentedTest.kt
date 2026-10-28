package com.angelstudio.newsapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.data.db.TopHeadlineDatabase
import com.angelstudio.newsapp.data.db.entity.Archive
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.db.entity.Source
import com.angelstudio.newsapp.data.network.ConnectivityInterceptor
import com.angelstudio.newsapp.data.network.NewsApiService
import com.angelstudio.newsapp.ui.MainActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class DependencyMigrationInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun composeActivityStartsOnTheNewsFeed() {
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.app_name)
        ).assertIsDisplayed()
    }

    @Test
    fun roomPreservesHeadlineAndArchiveTables() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TopHeadlineDatabase::class.java
        ).build()
        try {
            val dao = database.topHeadlineDao()
            val article = Article(
                1, "Author", "Content", "Description", "2026-10-03T12:30:00Z",
                Source("source", "Source"), "Headline", "https://example.com",
                "https://example.com/image.jpg"
            )
            dao.insert(listOf(article))
            dao.archive(
                Archive(
                    article.id, article.author, article.content, article.description,
                    article.publishedAt, article.source, article.title, article.url,
                    article.urlToImage
                )
            )
            assertEquals(3, database.openHelper.readableDatabase.version)
            database.openHelper.readableDatabase.query("SELECT title FROM TopHeadline").use {
                check(it.moveToFirst())
                assertEquals(article.title, it.getString(0))
            }
            database.openHelper.readableDatabase.query("SELECT title FROM Archive").use {
                check(it.moveToFirst())
                assertEquals(article.title, it.getString(0))
            }
            database.openHelper.readableDatabase.query(
                "SELECT isRead, savedAt FROM Archive"
            ).use {
                check(it.moveToFirst())
                assertEquals(0, it.getInt(0))
                assertTrue(it.getLong(1) > 0)
            }
            dao.deleteAll()
            database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM Archive").use {
                check(it.moveToFirst())
                assertEquals(0, it.getInt(0))
            }
        } finally {
            database.close()
        }
    }

    @Test
    fun retrofitSuspendServiceConvertsGsonResponse() = runBlocking {
        lateinit var request: Request
        val interceptor = object : ConnectivityInterceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                request = chain.request()
                val json = """
                    {
                        "status": "ok",
                        "totalResults": 1,
                        "articles": [{
                            "source": {"id": "source", "name": "Source"},
                            "author": null,
                            "title": "Headline",
                            "description": "Description",
                            "url": "https://example.com",
                            "urlToImage": null,
                            "publishedAt": "2026-10-03T12:30:00Z",
                            "content": "Content"
                        }]
                    }
                """.trimIndent()
                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(json.toResponseBody("application/json".toMediaType()))
                    .build()
            }
        }
        val response = NewsApiService(interceptor).getTopHeadlines("general", "us", "100")
        assertEquals("/v2/top-headlines", request.url.encodedPath)
        assertEquals("general", request.url.queryParameter("category"))
        assertEquals("us", request.url.queryParameter("country"))
        assertEquals("100", request.url.queryParameter("pagesize"))
        assertEquals("ok", response.status)
        assertEquals(1, response.totalResults)
        assertEquals("Headline", response.articles.single().title)
        assertEquals("Source", response.articles.single().source?.name)
    }

    @Test
    fun javaTimeParsesExistingNewsApiTimestamps() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val timestamp = LocalDateTime.parse("2026-10-03T12:30:00Z", formatter)
        assertEquals(12, timestamp.hour)
        assertEquals(3, timestamp.dayOfMonth)
    }
}
