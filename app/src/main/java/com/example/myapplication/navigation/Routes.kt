package com.example.myapplication.navigation

sealed class Routes(val route: String) {

    object Splash : Routes("splash")
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
    object Search : Routes("search")
    object WriteReviewHub : Routes("write_review_hub")
    object Profile : Routes("profile")
    object Notifications : Routes("notifications")
    object Feed : Routes("feed")
    object Saved : Routes("saved")

    // tab = "followers" o "following": la pestaña que se abre primero
    object FollowList : Routes("follow_list/{userId}?tab={tab}") {
        const val ARG_USER_ID = "userId"
        const val ARG_TAB = "tab"
        fun createRoute(userId: String, tab: String) = "follow_list/$userId?tab=$tab"
    }

    object UserProfile : Routes("user_profile/{userId}") {
        const val ARG_USER_ID = "userId"
        fun createRoute(userId: String) = "user_profile/$userId"
    }

    object ProductDetail : Routes("product_detail/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String) = "product_detail/$productId"
    }

    object ReviewsList : Routes("reviews_list/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String) = "reviews_list/$productId"
    }

    // reviewId vacío = crear reseña; con valor = editar esa reseña
    object NewReview : Routes("new_review?productId={productId}&reviewId={reviewId}") {
        const val ARG_PRODUCT_ID = "productId"
        const val ARG_REVIEW_ID = "reviewId"
        fun createRoute(productId: String? = null, reviewId: String? = null) =
            "new_review?productId=${productId.orEmpty()}&reviewId=${reviewId.orEmpty()}"
    }

    object ReviewPublished : Routes("review_published/{productId}/{rating}") {
        const val ARG_PRODUCT_ID = "productId"
        const val ARG_RATING = "rating"
        fun createRoute(productId: String, rating: Int) = "review_published/$productId/$rating"
    }
}
