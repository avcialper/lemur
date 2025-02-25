package com.avcialper.lemur.di

import com.avcialper.lemur.data.repository.remote.StorageApi
import com.avcialper.lemur.util.constants.Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .setLenient()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = provideHttpLoggingInterceptor()
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit.Builder {
        val client = provideOkHttpClient()
        val convertorFactory = GsonConverterFactory.create()
        return Retrofit.Builder()
            .baseUrl(Constant.IMG_BB_URL)
            .client(client)
            .addConverterFactory(convertorFactory)
    }

    @Provides
    @Singleton
    fun provideStorageService(retrofit: Retrofit.Builder): StorageApi {
        return retrofit
            .build()
            .create(StorageApi::class.java)
    }
}