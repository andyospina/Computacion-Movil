package com.example.myapplication.data

/** Ids de quienes siguen a un usuario y de a quienes él sigue. */
data class FollowStats(
    val followerIds: List<String> = emptyList(),
    val followingIds: List<String> = emptyList()
) {
    val followersCount: Int get() = followerIds.size
    val followingCount: Int get() = followingIds.size

    fun isFollowedBy(userId: String?): Boolean = userId != null && userId in followerIds
}
