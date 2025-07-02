package com.projectmicrocode.ecaprov2.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import replace.the.packagename.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiRequest

@Module
@InstallIn(SingletonComponent::class)
object ApiProvider {

    private const val BASE_URL = "localhost:8080"


    @Singleton
    @Provides
    @ApiRequest
    fun provideRetrofit(
        @ApplicationContext context: Context
    ): Retrofit {
        val chuckerInterceptor = ChuckerInterceptor.Builder(context).collector(
            ChuckerCollector(
                context = context,
                showNotification = true,
                retentionPeriod = RetentionManager.Period.ONE_HOUR
            )
        ).maxContentLength(250_000L).alwaysReadResponseBody(true).build()
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) httpClient.addInterceptor(logging)
        httpClient.readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS)
            .cache(null).addInterceptor(chuckerInterceptor)
        httpClient.addInterceptor { chain ->
            val request = chain.request()
            val interceptedRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer ").build()
            chain.proceed(interceptedRequest)
        }

        return Retrofit.Builder().baseUrl(BASE_URL).client(httpClient.build())
            .addConverterFactory(MoshiConverterFactory.create()).build()
    }

//    @Provides
//    @Singleton
//    fun provideThingspeakService(
//        @ApiRequest retrofit: Retrofit
//    ): ThingspeakService {
//        return retrofit.create(ThingspeakService::class.java)
//    }

}

