package com.bol.instantapp.di.modules

import com.bol.instantapp.api.FourSquareApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
class ApiModule{
    @Provides
    @Singleton
    fun providesCatalogApi(retrofit: Retrofit): FourSquareApi {
        return retrofit.create<FourSquareApi>(FourSquareApi::class.java)
    }
}