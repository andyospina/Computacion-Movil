package com.example.myapplication.data.di

import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.datasource.impl.ProductRetrofitDataSource
import com.example.myapplication.data.datasource.impl.ReviewRetrofitDataSource
import com.example.myapplication.data.datasource.impl.UserRetrofitDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Cambiar de Retrofit a otra tecnología solo requiere cambiar estos bindings. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindProductDataSource(impl: ProductRetrofitDataSource): ProductRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindReviewDataSource(impl: ReviewRetrofitDataSource): ReviewRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserDataSource(impl: UserRetrofitDataSource): UserRemoteDataSource
}
