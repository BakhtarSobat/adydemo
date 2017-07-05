package com.bol.instantapp.di

import android.app.Application
import android.content.Context
import com.adyen.bsobat.di.modules.LocationModule
import com.adyen.bsobat.livedata.LocationLiveData
import com.bol.instantapp.Repository.FourSquareRepository
import com.bol.instantapp.di.modules.ApiModule
import com.bol.instantapp.di.modules.AppModule
import com.bol.instantapp.di.modules.NetModule
import com.bol.instantapp.di.modules.RepositoryModule
import com.bol.instantapp.viewmodel.MainActivityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = arrayOf(AppModule::class, NetModule::class, RepositoryModule::class, ApiModule::class, LocationModule::class)
)
interface AppComponent {
    fun inject(viewModelModule: MainActivityViewModel)
    fun inject(activity: Context)

    fun provideFourSquareRepository(): FourSquareRepository
    fun provideLocationLiveData(): LocationLiveData

    companion object Factory{
        fun create(app: Application, baseUrl: String): AppComponent {
            val appComponent = DaggerAppComponent.builder().
                    appModule(AppModule(app)).
                    apiModule(ApiModule()).
                    netModule(NetModule(baseUrl)).
                    locationModule(LocationModule()).
                    repositoryModule(RepositoryModule()).
                    build();
            return appComponent
        }
    }
}