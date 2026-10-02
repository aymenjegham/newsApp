package com.angelstudio.newsapp

import android.app.Application
import com.angelstudio.newsapp.data.db.TopHeadlineDatabase
import com.angelstudio.newsapp.data.network.*
import com.angelstudio.newsapp.data.repository.TopHeadlineRepository
import com.angelstudio.newsapp.data.repository.TopHeadlineRepositoryImpl
import com.angelstudio.newsapp.ui.archive.ArchiveViewModelFactory
import com.angelstudio.newsapp.ui.feed.FeedFragmentViewModelFactory
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bindSingleton
import org.kodein.di.bindProvider
import org.kodein.di.instance

class NewsApplication : Application(), DIAware {
    override val di = DI.lazy {

        import(androidXModule(this@NewsApplication))

        bindSingleton { TopHeadlineDatabase(instance()) }
        bindSingleton { instance<TopHeadlineDatabase>().topHeadlineDao() }
        bindSingleton<ConnectivityInterceptor> { ConnectivityInterceptorImpl(instance()) }
        bindSingleton { NewsApiService(instance()) }
        bindSingleton<TopHeadlineDataSource> { TopHeadlineDataSourceImpl(instance()) }
        bindSingleton<TopHeadlineRepository> { TopHeadlineRepositoryImpl(instance(), instance(), instance()) }


        bindProvider { FeedFragmentViewModelFactory(instance()) }
        bindProvider { ArchiveViewModelFactory(instance()) }



    }

}
