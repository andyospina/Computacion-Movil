package com.example.myapplication.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRemoteDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {

    suspend fun uploadImage(path: String, imageUri: Uri): String {
        val reference = storage.reference.child(path)
        reference.putFile(imageUri).await()
        return reference.downloadUrl.await().toString()
    }
}
