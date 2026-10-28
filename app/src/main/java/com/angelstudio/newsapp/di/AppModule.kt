package com.angelstudio.newsapp.di

import android.content.Context
import androidx.room.Room
import com.angelstudio.newsapp.data.db.TopHeadlineDao
import com.angelstudio.newsapp.data.db.TopHeadlineDatabase
import com.angelstudio.newsapp.data.network.ConnectivityInterceptor
import com.angelstudio.newsapp.data.network.ConnectivityInterceptorImpl
import com.angelstudio.newsapp.data.network.NewsApiService
import com.angelstudio.newsapp.data.repository.NewsRepositoryImpl
import com.angelstudio.newsapp.data.repository.SettingsRepositoryImpl
import com.angelstudio.newsapp.domain.repository.NewsRepository
import com.angelstudio.newsapp.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindings {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(implementation: NewsRepositoryImpl): NewsRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(implementation: SettingsRepositoryImpl): SettingsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TopHeadlineDatabase =
        Room.databaseBuilder(context, TopHeadlineDatabase::class.java, "topheadline.db")
            .addMigrations(TopHeadlineDatabase.MIGRATION_1_2, TopHeadlineDatabase.MIGRATION_2_3)
            .build()

    @Provides
    fun provideTopHeadlineDao(database: TopHeadlineDatabase): TopHeadlineDao =
        database.topHeadlineDao()

    @Provides
    @Singleton
    fun provideConnectivityInterceptor(
        @ApplicationContext context: Context
    ): ConnectivityInterceptor = ConnectivityInterceptorImpl(context)

    @Provides
    @Singleton
    fun provideNewsApiService(interceptor: ConnectivityInterceptor): NewsApiService =
        NewsApiService(interceptor)
}
