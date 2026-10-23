package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.FollowRemoteDataSource
import com.example.myapplication.data.dto.FirestoreFollowDto
import com.example.myapplication.data.dto.followDocumentId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FOLLOWS_COLLECTION = "follows"

class FollowFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : FollowRemoteDataSource {

    private val follows get() = firestore.collection(FOLLOWS_COLLECTION)

    override fun observeFollowerIds(userId: String): Flow<List<String>> =
        follows.whereEqualTo("followedId", userId).observe()
            .map { snapshot -> snapshot.toObjects(FirestoreFollowDto::class.java).map { it.followerId } }

    override fun observeFollowingIds(userId: String): Flow<List<String>> =
        follows.whereEqualTo("followerId", userId).observe()
            .map { snapshot -> snapshot.toObjects(FirestoreFollowDto::class.java).map { it.followedId } }

    // Un solo documento por relación: seguir es crearlo y dejar de seguir es borrarlo.
    override suspend fun follow(followerId: String, followedId: String) {
        follows.document(followDocumentId(followerId, followedId))
            .set(FirestoreFollowDto(followerId = followerId, followedId = followedId))
            .await()
    }

    override suspend fun unfollow(followerId: String, followedId: String) {
        follows.document(followDocumentId(followerId, followedId)).delete().await()
    }
}
