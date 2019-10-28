package com.angelstudio.newsapp

import android.app.Application
import com.angelstudio.newsapp.data.db.TopHeadlineDatabase
import com.angelstudio.newsapp.data.network.*
import com.angelstudio.newsapp.data.repository.TopHeadlineRepository
import com.angelstudio.newsapp.data.repository.TopHeadlineRepositoryImpl
import com.angelstudio.newsapp.ui.feed.FeedFragmentViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class NewsApplication : Application(),KodeinAware{
    override val kodein=Kodein.lazy {

        import(androidXModule(this@NewsApplication))

        bind() from singleton { TopHeadlineDatabase(instance()) }
        bind() from singleton {instance<TopHeadlineDatabase>().topHeadlineDao() }
        bind<ConnectivityInterceptor>() with singleton {ConnectivityInterceptorImpl(instance())}
        bind() from singleton { NewsApiService(instance()) }
        bind<TopHeadlineDataSource>() with singleton {TopHeadlineDataSourceImpl(instance())}
        bind<TopHeadlineRepository>() with singleton {TopHeadlineRepositoryImpl(instance(),instance())}
        bind() from provider {FeedFragmentViewModelFactory(instance())}
    }
    override fun onCreate() {
        super.onCreate()
    }
}
