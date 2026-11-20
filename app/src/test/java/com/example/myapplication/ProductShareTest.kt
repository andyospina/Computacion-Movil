package com.example.myapplication

import com.example.myapplication.data.Product
import com.example.myapplication.ui.share.buildProductShareSubject
import com.example.myapplication.ui.share.buildProductShareText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Texto que se envía al compartir un producto (la plantilla real vive en R.string.share_product_text). */
class ProductShareTest {

    private val template = "%1\$s · %6\$s\n%2\$s %3\$s (%4\$d reseñas)\n%5\$d%% de los compradores lo recomiendan\nMíralo en %7\$s"

    private val product = Product(
        id = "p1", name = "Grabadora de voz AI", category = "Tecnología", rating = 4.8,
        reviewCount = 273, recommendPercent = 88, ratingBreakdown = emptyList()
    )

    @Test
    fun buildProductShareText_includesNameCategoryRatingReviewsAndRecommendation() {
        assertEquals(
            "Grabadora de voz AI · Tecnología\n★★★★★ 4.8 (273 reseñas)\n88% de los compradores lo recomiendan\nMíralo en ReviewLab",
            buildProductShareText(product, template, "ReviewLab")
        )
    }

    @Test
    fun buildProductShareText_roundsTheStars() {
        assertTrue(buildProductShareText(product.copy(rating = 3.4), template, "ReviewLab").contains("★★★☆☆ 3.4"))
        assertTrue(buildProductShareText(product.copy(rating = 3.5), template, "ReviewLab").contains("★★★★☆ 3.5"))
    }

    @Test
    fun buildProductShareText_worksForAProductWithoutReviews() {
        val text = buildProductShareText(product.copy(rating = 0.0, reviewCount = 0, recommendPercent = 0), template, "ReviewLab")

        assertTrue(text.contains("☆☆☆☆☆ 0.0 (0 reseñas)"))
        assertTrue(text.contains("0% de los compradores"))
    }

    @Test
    fun buildProductShareSubject_namesTheProduct() {
        assertEquals("Grabadora de voz AI en ReviewLab", buildProductShareSubject(product, "%1\$s en ReviewLab"))
    }
}
