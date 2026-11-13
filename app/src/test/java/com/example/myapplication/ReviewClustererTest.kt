package com.example.myapplication

import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.Review
import com.example.myapplication.ui.screens.Map.clusterReviews
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewClustererTest {

    private fun review(id: String, lat: Double, lng: Double, located: Boolean = true) = Review(
        id = id, productId = "p", author = "a", verifiedBuyer = false, rating = 4, title = "t", body = "b",
        date = "", usefulCount = 0, recommended = true,
        location = if (located) GeoLocation(lat, lng) else null
    )

    @Test
    fun closeReviews_mergeIntoOneClusterWithCountAndAveragePosition_whenZoomedOut() {
        val clusters = clusterReviews(
            listOf(review("a", 4.7100, -74.0700), review("b", 4.7102, -74.0702), review("c", 4.7101, -74.0701)),
            zoom = 10.0
        )

        val cluster = clusters.single()
        assertEquals(3, cluster.reviews.size)
        assertEquals(4.7101, cluster.latitude, 1e-9)
        assertTrue(!cluster.isSingle)
    }

    @Test
    fun theSameReviewsSeparate_whenTheUserZoomsIn() {
        val reviews = listOf(review("a", 4.7100, -74.0700), review("b", 4.7110, -74.0710))

        assertEquals(1, clusterReviews(reviews, zoom = 10.0).size)
        assertEquals(2, clusterReviews(reviews, zoom = 16.0).size)
    }

    @Test
    fun farAwayReviews_neverMerge() {
        val clusters = clusterReviews(listOf(review("bogota", 4.711, -74.072), review("madrid", 40.417, -3.704)), zoom = 4.0)

        assertEquals(2, clusters.size)
        assertTrue(clusters.all { it.isSingle })
    }

    @Test
    fun theSelectedReview_isNeverGrouped() {
        val reviews = listOf(review("a", 4.7100, -74.0700), review("b", 4.7101, -74.0701), review("c", 4.7102, -74.0702))

        val clusters = clusterReviews(reviews, zoom = 8.0, selectedId = "b")

        assertEquals(2, clusters.size)
        assertEquals(listOf("b"), clusters.single { it.isSingle }.reviews.map { it.id })
        assertEquals(setOf("a", "c"), clusters.single { !it.isSingle }.reviews.map { it.id }.toSet())
    }

    @Test
    fun atStreetZoom_nothingIsGrouped() {
        val reviews = listOf(review("a", 4.7100, -74.0700), review("b", 4.7100, -74.0700))

        assertEquals(2, clusterReviews(reviews, zoom = 18.0).size)
    }

    @Test
    fun reviewsWithoutLocation_areIgnored_andEmptyInputIsEmpty() {
        assertEquals(1, clusterReviews(listOf(review("a", 1.0, 1.0), review("x", 0.0, 0.0, located = false)), zoom = 5.0).size)
        assertTrue(clusterReviews(emptyList(), zoom = 5.0).isEmpty())
    }
}
