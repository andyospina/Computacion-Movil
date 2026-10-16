package com.example.myapplication.data.di

import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.datasource.impl.ProductFirestoreDataSource
import com.example.myapplication.data.datasource.impl.ReviewFirestoreDataSource
import com.example.myapplication.data.datasource.impl.UserFirestoreDataSource
import com.example.myapplication.data.identity.CurrentUserIdProvider
import com.example.myapplication.data.identity.FirebaseCurrentUserIdProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Punto único de selección del backend de datos: Firestore (actual) o Retrofit. Cambiar de uno a otro
 * solo requiere cambiar estos bindings (y el proveedor de identidad al final).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindProductDataSource(impl: ProductFirestoreDataSource): ProductRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindReviewDataSource(impl: ReviewFirestoreDataSource): ReviewRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserDataSource(impl: UserFirestoreDataSource): UserRemoteDataSource

    // Con Retrofit usar RetrofitCurrentUserIdProvider
    @Binds
    @Singleton
    abstract fun bindCurrentUserIdProvider(impl: FirebaseCurrentUserIdProvider): CurrentUserIdProvider
}
