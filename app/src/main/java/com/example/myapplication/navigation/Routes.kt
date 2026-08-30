package com.example.myapplication.navigation

sealed class Routes(val route: String) {

    object Login : Routes("login")
    object Home : Routes("home")
    object Search : Routes("search")
    object WriteReviewHub : Routes("write_review_hub")
    object Profile : Routes("profile")
    object Notifications : Routes("notifications")

    object ProductDetail : Routes("product_detail/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String) = "product_detail/$productId"
    }

    object ReviewsList : Routes("reviews_list/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String) = "reviews_list/$productId"
    }

    object NewReview : Routes("new_review?productId={productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String? = null) =
            "new_review?productId=${productId.orEmpty()}"
    }

    object ReviewPublished : Routes("review_published/{productId}/{rating}") {
        const val ARG_PRODUCT_ID = "productId"
        const val ARG_RATING = "rating"
        fun createRoute(productId: String, rating: Int) = "review_published/$productId/$rating"
    }
}
