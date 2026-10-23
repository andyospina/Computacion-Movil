package com.example.myapplication.data.repository

import com.example.myapplication.R
import com.example.myapplication.data.FollowStats
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.FollowRemoteDataSource
import com.example.myapplication.data.identity.CurrentUserIdProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FollowRepository @Inject constructor(
    private val remoteDataSource: FollowRemoteDataSource,
    private val currentUserId: CurrentUserIdProvider
) {

    /** Seguidores y seguidos de [userId] en tiempo real (los contadores cambian al instante). */
    fun observeFollowStats(userId: String): Flow<Result<FollowStats>> =
        combine(
            remoteDataSource.observeFollowerIds(userId),
            remoteDataSource.observeFollowingIds(userId)
        ) { followers, following -> FollowStats(followerIds = followers, followingIds = following) }
            .asResult()

    /** A quiénes sigue el usuario de la sesión (para el feed «Siguiendo»). */
    fun observeMyFollowingIds(): Flow<Result<List<String>>> {
        val uid = currentUserId.currentUserId()
            ?: return flowOf(Result.Failure(NotAuthenticatedException(), R.string.error_session_required))
        return remoteDataSource.observeFollowingIds(uid).asResult()
    }

    suspend fun follow(userId: String): Result<Unit> = runCatchingApi {
        val me = currentUserId.currentUserId() ?: throw NotAuthenticatedException()
        require(me != userId) { "Un usuario no puede seguirse a sí mismo" }
        remoteDataSource.follow(followerId = me, followedId = userId)
    }

    suspend fun unfollow(userId: String): Result<Unit> = runCatchingApi {
        val me = currentUserId.currentUserId() ?: throw NotAuthenticatedException()
        remoteDataSource.unfollow(followerId = me, followedId = userId)
    }
}
