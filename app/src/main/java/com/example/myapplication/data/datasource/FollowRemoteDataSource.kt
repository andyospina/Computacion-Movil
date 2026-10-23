package com.example.myapplication.data.datasource

import kotlinx.coroutines.flow.Flow

interface FollowRemoteDataSource {
    /** Ids de los usuarios que siguen a [userId], en tiempo real. */
    fun observeFollowerIds(userId: String): Flow<List<String>>

    /** Ids de los usuarios a los que [userId] sigue, en tiempo real. */
    fun observeFollowingIds(userId: String): Flow<List<String>>

    suspend fun follow(followerId: String, followedId: String)
    suspend fun unfollow(followerId: String, followedId: String)
}
