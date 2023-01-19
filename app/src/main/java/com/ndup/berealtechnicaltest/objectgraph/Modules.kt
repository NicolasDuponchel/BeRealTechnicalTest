package com.ndup.berealtechnicaltest.objectgraph

import com.ndup.berealtechnicaltest.presentation.MainModel
import com.ndup.berealtechnicaltest.repository.ApiServices
import com.ndup.berealtechnicaltest.repository.IRepository
import com.ndup.berealtechnicaltest.repository.Repository
import com.ndup.berealtechnicaltest.repository.ServiceFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PresentationModule {

    @Provides
    fun provideMainModel() = MainModel(
        currentUser = null,
        currentPath = emptyList(),
        items = emptyList(),
    )

}


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    companion object {
        @Singleton
        @Provides
        fun provideApiServices(): ApiServices = ServiceFactory.service
    }

    @Binds
    abstract fun provideIRepository(repo: Repository): IRepository

}
