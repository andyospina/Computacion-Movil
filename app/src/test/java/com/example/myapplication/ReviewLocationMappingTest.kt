package com.example.myapplication

import com.example.myapplication.data.GeoLocation
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.NewReviewDto
import com.example.myapplication.data.dto.toFirestoreDto
import com.google.firebase.firestore.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class ReviewLocationMappingTest {

    private fun newReview(location: GeoLocation?) = NewReviewDto(
        productId = "p1", productName = "Audífonos", userId = "u1", authorName = "Ana", authorUsername = "ana",
        authorImage = null, rating = 5, title = "t", body = "b", recommended = true, location = location
    )

    @Test
    fun newReviewLocation_becomesAFirestoreGeoPoint() {
        val dto = newReview(GeoLocation(4.711, -74.072)).toFirestoreDto()

        assertEquals(GeoPoint(4.711, -74.072), dto.location)
    }

    @Test
    fun reviewWithoutLocation_staysWithoutLocation() {
        assertNull(newReview(null).toFirestoreDto().location)
    }

    @Test
    fun outOfRangeCoordinates_areDiscardedInsteadOfBreakingThePublish() {
        // GeoPoint lanza IllegalArgumentException con latitudes fuera de ±90: no debe tumbar la publicación
        assertNull(newReview(GeoLocation(123.0, 10.0)).toFirestoreDto().location)
    }

    @Test
    fun firestoreDocument_mapsToTheDomainLocationAndTimestamp() {
        val created = Date(1_700_000_000_000)
        val review = FirestoreReviewDto(id = "r1", location = GeoPoint(6.244, -75.581), createdAt = created).toReview(null)

        assertEquals(GeoLocation(6.244, -75.581), review.location)
        assertEquals(created.time, review.createdAtMillis)
    }

    @Test
    fun legacyDocumentWithoutLocation_hasNullLocation() {
        val review = FirestoreReviewDto(id = "old").toReview(null)

        assertNull(review.location)
        assertNull(review.createdAtMillis)
    }

    @Test
    fun geoLocationValidity() {
        assertTrue(GeoLocation(90.0, 180.0).isValid)
        assertFalse(GeoLocation(90.1, 0.0).isValid)
        assertFalse(GeoLocation(0.0, -180.5).isValid)
    }
}
