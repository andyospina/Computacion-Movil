package com.example.myapplication.data

object LocalProductProvider {

    const val DEFAULT_PRODUCT_ID = "grabadora-voz-ai"

    val products = listOf(
        Product(
            id = DEFAULT_PRODUCT_ID,
            name = "Grabadora de voz AI",
            category = "Tecnología",
            rating = 4.8,
            reviewCount = 273,
            recommendPercent = 88,
            ratingBreakdown = listOf(5 to 0.88f, 4 to 0.55f, 3 to 0.40f, 2 to 0.28f, 1 to 0.12f)
        ),
        Product(
            id = "serum-vitamina-c",
            name = "Sérum facial vitamina C",
            category = "Belleza",
            rating = 4.2,
            reviewCount = 158,
            recommendPercent = 79,
            ratingBreakdown = listOf(5 to 0.60f, 4 to 0.45f, 3 to 0.30f, 2 to 0.15f, 1 to 0.08f)
        ),
        Product(
            id = "cafetera-espresso-mini",
            name = "Cafetera espresso mini",
            category = "Hogar",
            rating = 4.5,
            reviewCount = 96,
            recommendPercent = 85,
            ratingBreakdown = listOf(5 to 0.70f, 4 to 0.40f, 3 to 0.20f, 2 to 0.10f, 1 to 0.05f)
        ),
        Product(
            id = "audifonos-inalambricos-pro",
            name = "Audífonos inalámbricos Pro",
            category = "Tecnología",
            rating = 3.9,
            reviewCount = 412,
            recommendPercent = 68,
            ratingBreakdown = listOf(5 to 0.45f, 4 to 0.35f, 3 to 0.30f, 2 to 0.20f, 1 to 0.15f)
        ),
        Product(
            id = "audifonos-deportivos-x2",
            name = "Audífonos deportivos X2",
            category = "Tecnología",
            rating = 4.4,
            reviewCount = 87,
            recommendPercent = 81,
            ratingBreakdown = listOf(5 to 0.58f, 4 to 0.38f, 3 to 0.20f, 2 to 0.10f, 1 to 0.06f)
        ),
        Product(
            id = "audifonos-cancelacion",
            name = "Audífonos con cancelación",
            category = "Tecnología",
            rating = 4.7,
            reviewCount = 230,
            recommendPercent = 90,
            ratingBreakdown = listOf(5 to 0.80f, 4 to 0.50f, 3 to 0.25f, 2 to 0.10f, 1 to 0.04f)
        )
    )

    fun findById(id: String): Product =
        products.find { it.id == id } ?: products.first()

    fun search(query: String): List<Product> =
        if (query.isBlank()) emptyList()
        else products.filter { it.name.normalizeForSearch().contains(query.normalizeForSearch()) }

    private fun String.normalizeForSearch(): String =
        java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()

    fun trending(): List<Product> = products.take(4)
}
