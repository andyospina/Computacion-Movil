package com.example.myapplication.data.di

import com.example.myapplication.data.datasource.CommentRemoteDataSource
import com.example.myapplication.data.datasource.DeviceLocationDataSource
import com.example.myapplication.data.datasource.FollowRemoteDataSource
import com.example.myapplication.data.datasource.NotificationRemoteDataSource
import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.datasource.ReportRemoteDataSource
import com.example.myapplication.data.datasource.ReviewRemoteDataSource
import com.example.myapplication.data.datasource.SavedRemoteDataSource
import com.example.myapplication.data.datasource.UserRemoteDataSource
import com.example.myapplication.data.datasource.impl.CommentFirestoreDataSource
import com.example.myapplication.data.datasource.impl.FollowFirestoreDataSource
import com.example.myapplication.data.datasource.impl.FusedDeviceLocationDataSource
import com.example.myapplication.data.datasource.impl.NotificationFirestoreDataSource
import com.example.myapplication.data.datasource.impl.ProductFirestoreDataSource
import com.example.myapplication.data.datasource.impl.ReportFirestoreDataSource
import com.example.myapplication.data.datasource.impl.ReviewFirestoreDataSource
import com.example.myapplication.data.datasource.impl.SavedFirestoreDataSource
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

    // Retrofit: FollowRetrofitDataSource (sin seguidores en el backend Express)
    @Binds
    @Singleton
    abstract fun bindFollowDataSource(impl: FollowFirestoreDataSource): FollowRemoteDataSource

    // Retrofit: NotificationRetrofitDataSource (sin tokens ni bandeja en el backend Express)
    @Binds
    @Singleton
    abstract fun bindNotificationDataSource(impl: NotificationFirestoreDataSource): NotificationRemoteDataSource

    // Solo Firestore: el backend Express no tiene productos guardados
    @Binds
    @Singleton
    abstract fun bindSavedDataSource(impl: SavedFirestoreDataSource): SavedRemoteDataSource

    // Solo Firestore: el backend Express no tiene comentarios en las reseñas
    @Binds
    @Singleton
    abstract fun bindCommentDataSource(impl: CommentFirestoreDataSource): CommentRemoteDataSource

    // Solo Firestore: el backend Express no tiene reportes de reseñas
    @Binds
    @Singleton
    abstract fun bindReportDataSource(impl: ReportFirestoreDataSource): ReportRemoteDataSource

    // Ubicación del dispositivo (Fused Location Provider)
    @Binds
    @Singleton
    abstract fun bindDeviceLocationDataSource(impl: FusedDeviceLocationDataSource): DeviceLocationDataSource

    // Con Retrofit usar RetrofitCurrentUserIdProvider
    @Binds
    @Singleton
    abstract fun bindCurrentUserIdProvider(impl: FirebaseCurrentUserIdProvider): CurrentUserIdProvider
}
