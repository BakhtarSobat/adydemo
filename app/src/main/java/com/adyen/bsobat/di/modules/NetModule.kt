package com.bol.instantapp.di.modules

import android.app.Application
import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetModule (val baseUrl: String){
    val PARAM_CLIENT_ID = "client_id"
    val VALUE_CLIENT_ID = "4ZK0AD13KFW3RVIJSVRQUHXNPPLL4MINFYTABDEQUSCDJGUW"
    val PARAM_SECRET = "client_secret"
    val VALUE_SECRET = "DN0FT1KYUAIRWOWJNYM50QXV0LNY1EULCAKHBCOKWRL32X4D"
    val PARAM_VERSION = "v"
    val VALUE_VERSION = "20170705"

    @Provides
    @Singleton
    fun provideHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024L
        val cache = Cache(application.cacheDir, cacheSize)
        return cache
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(cache: Cache, interceptor: Interceptor): OkHttpClient {
        val client = OkHttpClient.Builder().addInterceptor(interceptor)
        client.cache(cache)
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor{
        return Interceptor { chain ->
            //copy and alter url
            val url = chain
                    .request()
                    .url()
                    .newBuilder()
                    .addQueryParameter(PARAM_CLIENT_ID, VALUE_CLIENT_ID)
                    .addQueryParameter(PARAM_SECRET, VALUE_SECRET)
                    .addQueryParameter(PARAM_VERSION, VALUE_VERSION)
                    .addQueryParameter("format", "json")
                    .build()

            //copy and alter request
            val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
            Log.d("URL", url.toString())
            chain.proceed(request)
        }
    }
}