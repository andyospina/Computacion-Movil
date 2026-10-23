package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.FollowRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** El backend Express no expone seguidores: las listas quedan vacías y seguir no está soportado. */
class FollowRetrofitDataSource @Inject constructor() : FollowRemoteDataSource {

    override fun observeFollowerIds(userId: String): Flow<List<String>> = flowOf(emptyList())

    override fun observeFollowingIds(userId: String): Flow<List<String>> = flowOf(emptyList())

    override suspend fun follow(followerId: String, followedId: String) {
        throw UnsupportedOperationException("El backend Express no tiene seguidores")
    }

    override suspend fun unfollow(followerId: String, followedId: String) {
        throw UnsupportedOperationException("El backend Express no tiene seguidores")
    }
}
