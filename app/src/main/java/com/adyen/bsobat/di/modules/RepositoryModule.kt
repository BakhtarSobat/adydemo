package com.bol.instantapp.di.modules

import com.bol.instantapp.Repository.FourSquareRepository
import com.bol.instantapp.api.FourSquareApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule{
    @Provides
    @Singleton
    fun provideCatalogRepository(api: FourSquareApi): FourSquareRepository{
        return FourSquareRepository(api)
    }
}