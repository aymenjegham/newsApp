package com.angelstudio.newsapp

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.data.db.TopHeadlineDatabase
import com.angelstudio.newsapp.data.db.entity.Archive
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.db.entity.Source
import com.angelstudio.newsapp.data.network.ConnectivityInterceptor
import com.angelstudio.newsapp.data.network.NewsApiService
import com.angelstudio.newsapp.data.network.TopHeadlineDataSourceImpl
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse
import com.angelstudio.newsapp.ui.MainActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class DependencyMigrationInstrumentedTest {

    @Test
    fun upgradedNavigationAndDiOpenArchiveAndSettings() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.main_root)).check(matches(isDisplayed()))
            scenario.onActivity { it.navController.navigate(R.id.archiveFragment) }
            onView(withId(R.id.recycler_view_archive)).check(matches(isDisplayed()))
            scenario.onActivity { it.navController.navigate(R.id.settingsFragment) }
            onView(withId(R.id.categories_list)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun roomPreservesHeadlineAndArchiveTables() {
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
            assertEquals(1, database.openHelper.readableDatabase.version)
            database.openHelper.readableDatabase.query("SELECT title FROM TopHeadline").use {
                check(it.moveToFirst())
                assertEquals(article.title, it.getString(0))
            }
            database.openHelper.readableDatabase.query("SELECT title FROM Archive").use {
                check(it.moveToFirst())
                assertEquals(article.title, it.getString(0))
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
    fun suspendNewsServiceReceivesHeadlineParameters() = runBlocking {
        var receivedParameters: List<String>? = null
        val service = object : NewsApiService {
            override suspend fun getTopHeadlines(
                category: String,
                country: String,
                pagesize: String
            ): TopHeadlineNewsResponse {
                receivedParameters = listOf(category, country, pagesize)
                return TopHeadlineNewsResponse(emptyList(), "ok", 0)
            }
        }
        TopHeadlineDataSourceImpl(service).fetchTopHeadline("general", "us", "100")
        assertEquals(listOf("general", "us", "100"), receivedParameters)
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
        assertEquals("Source", response.articles.single().source.name)
    }

    @Test
    fun javaTimeParsesExistingNewsApiTimestamps() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val timestamp = LocalDateTime.parse("2026-10-03T12:30:00Z", formatter)
        assertEquals(12, timestamp.hour)
        assertEquals(3, timestamp.dayOfMonth)
    }
}
