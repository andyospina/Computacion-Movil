package com.example.myapplication

import com.example.myapplication.data.Review
import com.example.myapplication.ui.share.SHARE_BODY_MAX_LENGTH
import com.example.myapplication.ui.share.buildReviewShareSubject
import com.example.myapplication.ui.share.buildReviewShareText
import com.example.myapplication.ui.share.starsText
import com.example.myapplication.ui.share.truncateForShare
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Texto que se envía al compartir una reseña (la plantilla real vive en R.string.share_review_text). */
class ReviewShareTest {

    private val template = "%1\$s reseñó «%2\$s» en %6\$s\n%3\$s\n\n%4\$s\n%5\$s"

    private val review = Review(
        id = "r1", productId = "p1", author = "Emily", verifiedBuyer = true, rating = 4,
        title = "Muy buena", body = "Dura todo el día", date = "", usefulCount = 0, recommended = true,
        productName = "Audífonos Pro"
    )

    @Test
    fun starsText_fillsAndEmptiesStars() {
        assertEquals("★★★★☆", starsText(4))
        assertEquals("☆☆☆☆☆", starsText(0))
        assertEquals("★★★★★", starsText(5))
    }

    @Test
    fun starsText_clampsOutOfRangeRatings() {
        assertEquals("★★★★★", starsText(9))
        assertEquals("☆☆☆☆☆", starsText(-3))
    }

    @Test
    fun truncateForShare_keepsShortTextAndTrimsIt() {
        assertEquals("Hola", truncateForShare("  Hola  "))
    }

    @Test
    fun truncateForShare_cutsLongTextWithEllipsis() {
        val result = truncateForShare("a".repeat(SHARE_BODY_MAX_LENGTH + 50))
        assertEquals(SHARE_BODY_MAX_LENGTH, result.length)
        assertTrue(result.endsWith("…"))
    }

    @Test
    fun truncateForShare_doesNotCutTextExactlyAtTheLimit() {
        val exact = "b".repeat(SHARE_BODY_MAX_LENGTH)
        assertEquals(exact, truncateForShare(exact))
    }

    @Test
    fun buildReviewShareText_includesAuthorProductStarsTitleAndBody() {
        val text = buildReviewShareText(review, template, "ReviewLab")

        assertEquals(
            "Emily reseñó «Audífonos Pro» en ReviewLab\n★★★★☆\n\nMuy buena\nDura todo el día",
            text
        )
    }

    @Test
    fun buildReviewShareText_usesTheTitleWhenThereIsNoProductName() {
        val text = buildReviewShareText(review.copy(productName = ""), template, "ReviewLab")

        assertTrue(text.contains("«Muy buena»"))
    }

    @Test
    fun buildReviewShareText_truncatesAVeryLongBody() {
        val text = buildReviewShareText(review.copy(body = "x".repeat(2000)), template, "ReviewLab")

        assertFalse(text.contains("x".repeat(SHARE_BODY_MAX_LENGTH + 1)))
        assertTrue(text.contains("…"))
    }

    @Test
    fun buildReviewShareText_fallsBackToTheAppNameWhenAuthorIsBlank() {
        val text = buildReviewShareText(review.copy(author = " "), template, "ReviewLab")

        assertTrue(text.startsWith("ReviewLab reseñó"))
    }

    @Test
    fun buildReviewShareSubject_namesTheProduct() {
        assertEquals("Reseña de Audífonos Pro en ReviewLab", buildReviewShareSubject(review, "Reseña de %1\$s en ReviewLab"))
    }
}
