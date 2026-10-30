package com.example.myapplication.data.datasource

import kotlinx.coroutines.flow.Flow

interface SavedRemoteDataSource {
    /** Ids de los productos que [userId] guardó, del último guardado al primero, en tiempo real. */
    fun observeSavedProductIds(userId: String): Flow<List<String>>

    suspend fun save(userId: String, productId: String)
    suspend fun unsave(userId: String, productId: String)
}
