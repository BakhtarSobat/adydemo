package com.adyen.bsobat

import android.app.Application
import com.bol.instantapp.di.AppComponent


class App: Application() {
    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponent.create(this, "https://api.foursquare.com/");
    }


}