package com.example.myapplication.data.di

import com.example.myapplication.data.ApiConfig
import com.example.myapplication.data.services.ProductRetrofitService
import com.example.myapplication.data.services.ReviewRetrofitService
import com.example.myapplication.data.services.UserRetrofitService
import com.google.firebase.auth.FirebaseAuth
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideProductRetrofitService(retrofit: Retrofit): ProductRetrofitService =
        retrofit.create(ProductRetrofitService::class.java)

    @Provides
    @Singleton
    fun provideReviewRetrofitService(retrofit: Retrofit): ReviewRetrofitService =
        retrofit.create(ReviewRetrofitService::class.java)

    @Provides
    @Singleton
    fun provideUserRetrofitService(retrofit: Retrofit): UserRetrofitService =
        retrofit.create(UserRetrofitService::class.java)
}
