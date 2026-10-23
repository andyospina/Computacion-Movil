package com.example.myapplication

import com.example.myapplication.data.FollowStats
import com.example.myapplication.data.NotificationType
import com.example.myapplication.data.Review
import com.example.myapplication.data.User
import com.example.myapplication.data.dto.FirestoreNotificationDto
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.ReviewDto
import com.example.myapplication.data.dto.followDocumentId
import com.example.myapplication.data.dto.toFirestoreDto
import com.example.myapplication.data.withReviewStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Sprint 11: likes, seguidores y notificaciones. */
class SocialMappingTest {

    @Test
    fun firestoreReview_likesCountIsSizeOfLikedBy() {
        val review = FirestoreReviewDto(likedBy = listOf("a", "b", "c")).toReview("z")

        assertEquals(3, review.likesCount)
        assertFalse(review.likedByMe)
    }

    @Test
    fun firestoreReview_knowsIfCurrentUserLiked() {
        val dto = FirestoreReviewDto(likedBy = listOf("a", "me"))

        assertTrue(dto.toReview("me").likedByMe)
        assertFalse(dto.toReview(null).likedByMe)
    }

    @Test
    fun firestoreReview_withoutLikedByHasNoLikes() {
        val review = FirestoreReviewDto().toReview("me")

        assertEquals(0, review.likesCount)
        assertFalse(review.likedByMe)
    }

    @Test
    fun newReview_startsWithoutLikes() {
        val dto = NewReviewDto(
            productId = "p1", productName = "X", userId = "uid1", authorName = "Ada",
            authorUsername = "ada", authorImage = null, rating = 5, title = "t", body = "b",
            recommended = true
        ).toFirestoreDto()

        assertTrue(dto.likedBy.isEmpty())
    }

    @Test
    fun retrofitReview_hasNoLikes() {
        val review = ReviewDto(
            id = 1, rating = 4, title = "t", body = "b", userId = 2, productId = 3
        ).toReview("2")

        assertEquals(0, review.likesCount)
        assertFalse(review.likedByMe)
    }

    @Test
    fun followDocumentId_isFollowerThenFollowed() {
        assertEquals("ana_beto", followDocumentId(followerId = "ana", followedId = "beto"))
    }

    @Test
    fun followStats_countsAndIsFollowedBy() {
        val stats = FollowStats(followerIds = listOf("a", "b"), followingIds = listOf("c"))

        assertEquals(2, stats.followersCount)
        assertEquals(1, stats.followingCount)
        assertTrue(stats.isFollowedBy("a"))
        assertFalse(stats.isFollowedBy("c"))
        assertFalse(stats.isFollowedBy(null))
    }

    @Test
    fun userStats_sumLikesReceived() {
        val user = User(
            name = "Ada", initials = "A", memberSinceYear = 2025,
            reviewCount = 0, usefulVotes = 0, averageRating = 0.0
        )
        val reviews = listOf(review(rating = 5, likes = 2), review(rating = 4, likes = 3))

        val withStats = user.withReviewStats(reviews)

        assertEquals(2, withStats.reviewCount)
        assertEquals(5, withStats.usefulVotes)
        assertEquals(4.5, withStats.averageRating, 0.0)
    }

    @Test
    fun notification_mapsKnownTypes() {
        val like = FirestoreNotificationDto(
            id = "n1", type = "like", actorId = "u1", actorName = "Ada",
            reviewTitle = "Genial", productId = "p1"
        ).toNotification()
        val follow = FirestoreNotificationDto(type = "follow", read = true).toNotification()

        assertEquals(NotificationType.LIKE, like.type)
        assertEquals("Ada", like.actorName)
        assertEquals("p1", like.productId)
        assertFalse(like.read)
        assertEquals(NotificationType.FOLLOW, follow.type)
        assertTrue(follow.read)
    }

    @Test
    fun notification_unknownTypeDoesNotCrash() {
        assertEquals(NotificationType.UNKNOWN, FirestoreNotificationDto(type = "otro").toNotification().type)
    }

    private fun review(rating: Int, likes: Int) = Review(
        id = "r$rating", productId = "p", author = "a", verifiedBuyer = false, rating = rating,
        title = "t", body = "b", date = "", usefulCount = 0, recommended = true, likesCount = likes
    )
}
