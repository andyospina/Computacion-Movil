package com.example.myapplication.data

object LocalReviewProvider {

    val reviews = listOf(
        Review(
            id = "r1",
            productId = "grabadora-voz-ai",
            author = "Emily",
            verifiedBuyer = true,
            rating = 5,
            title = "100000/10 recommend!!",
            body = "I hadn't even had my recorder a week and I had used almost 300 minutes! With thousands of templates to choose from, it's extremely versatile...",
            date = "Jul 13, 2026",
            usefulCount = 2,
            recommended = true,
            hasPhoto = true
        ),
        Review(
            id = "r2",
            productId = "grabadora-voz-ai",
            author = "Carlos",
            verifiedBuyer = true,
            rating = 5,
            title = "Batería increíble",
            body = "La batería dura toda la semana y la transcripción es sorprendentemente precisa, incluso en reuniones largas.",
            date = "Ago 2, 2026",
            usefulCount = 14,
            recommended = true
        ),
        Review(
            id = "r3",
            productId = "grabadora-voz-ai",
            author = "Lucía",
            verifiedBuyer = true,
            rating = 4,
            title = "Cumple lo que promete",
            body = "Muy buena, aunque la app tarda un poco en sincronizar. Por lo demás, cumple todo lo que promete.",
            date = "Jul 28, 2026",
            usefulCount = 9,
            recommended = false
        ),
        Review(
            id = "r4",
            productId = "grabadora-voz-ai",
            author = "Diego",
            verifiedBuyer = true,
            rating = 3,
            title = "Correcta, sin más",
            body = "Hace su trabajo pero esperaba mejor micrófono en exteriores. El soporte respondió rápido.",
            date = "Jul 20, 2026",
            usefulCount = 3,
            recommended = false
        ),
        Review(
            id = "r5",
            productId = "serum-vitamina-c",
            author = "Emily",
            verifiedBuyer = true,
            rating = 3,
            title = "Bueno, pero el envase podría mejorar",
            body = "El producto cumple, pero el gotero del envase gotea un poco al usarlo.",
            date = "Jun 30, 2026",
            usefulCount = 5,
            recommended = false
        ),
        Review(
            id = "r6",
            productId = "cafetera-espresso-mini",
            author = "Emily",
            verifiedBuyer = true,
            rating = 0,
            title = "Borrador — sin terminar",
            body = "",
            date = "Ago 10, 2026",
            usefulCount = 0,
            recommended = false,
            status = ReviewStatus.DRAFT
        )
    )

    fun forProduct(productId: String): List<Review> =
        reviews.filter { it.productId == productId }

    fun byUser(author: String): List<Review> =
        reviews.filter { it.author == author }

    fun featuredForProduct(productId: String, limit: Int = 2): List<Review> =
        forProduct(productId).take(limit)
}
