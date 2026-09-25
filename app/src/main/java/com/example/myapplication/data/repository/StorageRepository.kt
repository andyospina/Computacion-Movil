package com.example.myapplication.data.repository

import android.net.Uri
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.AuthRemoteDataSource
import com.example.myapplication.data.datasource.StorageRemoteDataSource
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

private const val PROFILE_IMAGES_FOLDER = "profile_images"

class StorageRepository @Inject constructor(
    private val storageDataSource: StorageRemoteDataSource,
    private val authDataSource: AuthRemoteDataSource
) {

    suspend fun uploadProfileImage(imageUri: Uri): Result<String> {
        return try {
            val userId = authDataSource.currentUser?.uid ?: throw NotAuthenticatedException()
            val path = "$PROFILE_IMAGES_FOLDER/$userId.jpg"

            val downloadUrl = storageDataSource.uploadImage(path, imageUri)
            authDataSource.updatePhotoUrl(downloadUrl)

            Result.Success(downloadUrl)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e, e.toStorageErrorMessageRes())
        }
    }
}
