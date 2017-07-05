package com.adyen.bsobat.di.modules

import com.adyen.bsobat.livedata.LocationLiveData
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule{
    @Provides
    @Singleton
    fun providesLocation(context: android.app.Application): LocationLiveData {
        return LocationLiveData(context)
    }
}