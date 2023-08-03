package com.example.getdriver.di

import android.content.Context
import com.auto.getremont.data.remote.OrderRemoteDataSource
import com.auto.getremont.data.remote.OrderService
import com.example.getdriver.data.local.AppDatabase
import com.example.getdriver.data.local.dao.OrderDao
import com.example.getdriver.data.remote.YandexMapService
import com.example.getdriver.data.repository.OrdersRepository
import com.example.getdriver.data.repository.YandexMapRespository


import com.google.gson.Gson
import com.google.gson.GsonBuilder

import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(interceptor) // same for .addInterceptor(...)
        .connectTimeout(30, TimeUnit.SECONDS) //Backend is really slow
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl("http://172.20.10.2:8081/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideCharacterService(retrofit: Retrofit): OrderService = retrofit.create(OrderService::class.java)

    @Provides
    fun provideYandexService(retrofit: Retrofit): YandexMapService = retrofit.create(YandexMapService::class.java)

    @Singleton
    @Provides
    fun provideCharacterRemoteDataSource(blogsService: OrderService) = OrderRemoteDataSource(blogsService)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideCharacterDao(db: AppDatabase) = db.orderDao()

    @Singleton
    @Provides
    fun provideRepository(remoteDataSource: OrderService,
                          localDataSource: OrderDao) = OrdersRepository(remoteDataSource,localDataSource)

    @Singleton
    @Provides
    fun provideYandexMapRepository(
        yandexMapService: YandexMapService
    ) = YandexMapRespository(yandexMapService)

}