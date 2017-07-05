package com.bol.instantapp.di.modules

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val application: android.app.Application){

    @Provides
    @Singleton
    fun provideApplication() : android.app.Application {
        return application;
    }
}