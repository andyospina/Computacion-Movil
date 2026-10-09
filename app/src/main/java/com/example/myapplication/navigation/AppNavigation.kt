package com.example.myapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.components.AppBottomNavBar
import com.example.myapplication.ui.screens.Home.HomeScreen
import com.example.myapplication.ui.screens.Login.LoginScreen
import com.example.myapplication.ui.screens.NewReview.NewReviewScreen
import com.example.myapplication.ui.screens.Notifications.NotificationsScreen
import com.example.myapplication.ui.screens.ProductDetail.ProductDetailScreen
import com.example.myapplication.ui.screens.Profile.ProfileScreen
import com.example.myapplication.ui.screens.Register.RegisterScreen
import com.example.myapplication.ui.screens.ReviewPublished.ReviewPublishedScreen
import com.example.myapplication.ui.screens.Reviews.ReviewsListScreen
import com.example.myapplication.ui.screens.Search.SearchScreen
import com.example.myapplication.ui.screens.Splash.SplashScreen
import com.example.myapplication.ui.screens.UserProfile.UserProfileScreen
import com.example.myapplication.ui.screens.WriteReview.WriteReviewHubScreen

private val topLevelRoutes = bottomNavItems.map { it.route }.toSet()

@Composable
fun AppNavigation(
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in topLevelRoutes) {
                AppBottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Routes.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Routes.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToStart = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.Login.route) {
                LoginScreen(
                    onIniciarSesionClick = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onRegistrateClick = {
                        navController.navigate(Routes.Register.route)
                    }
                )
            }

            composable(Routes.Register.route) {
                RegisterScreen(
                    onRegistroExitoso = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onIniciarSesionClick = { navController.popBackStack() }
                )
            }

            composable(Routes.Home.route) {
                HomeScreen(
                    onSearchBarClick = { navController.navigate(Routes.Search.route) },
                    onProductoClick = { productId ->
                        navController.navigate(Routes.ProductDetail.createRoute(productId))
                    },
                    onAvatarClick = { navController.navigate(Routes.Profile.route) },
                    onNotificationsClick = { navController.navigate(Routes.Notifications.route) }
                )
            }

            composable(Routes.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onProductoClick = { productId ->
                        navController.navigate(Routes.ProductDetail.createRoute(productId))
                    }
                )
            }

            composable(Routes.WriteReviewHub.route) {
                WriteReviewHubScreen(
                    onEscribirResenaClick = {
                        navController.navigate(Routes.NewReview.createRoute())
                    },
                    onAvatarClick = { navController.navigate(Routes.Profile.route) }
                )
            }

            composable(Routes.Profile.route) {
                ProfileScreen(
                    modoOscuro = modoOscuro,
                    onModoOscuroChange = onModoOscuroChange,
                    onEditResena = { productId, reviewId ->
                        navController.navigate(Routes.NewReview.createRoute(productId, reviewId))
                    },
                    onLoggedOut = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.Notifications.route) {
                NotificationsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ProductDetail.route,
                arguments = listOf(navArgument(Routes.ProductDetail.ARG_PRODUCT_ID) { type = NavType.StringType })
            ) { backStack ->
                val productId = backStack.arguments?.getString(Routes.ProductDetail.ARG_PRODUCT_ID).orEmpty()

                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onEscribirResenaClick = { id ->
                        navController.navigate(Routes.NewReview.createRoute(id))
                    },
                    onVerTodasResenasClick = { id ->
                        navController.navigate(Routes.ReviewsList.createRoute(id))
                    },
                    onUserClick = { userId ->
                        navController.navigate(Routes.UserProfile.createRoute(userId))
                    }
                )
            }

            composable(
                route = Routes.ReviewsList.route,
                arguments = listOf(navArgument(Routes.ReviewsList.ARG_PRODUCT_ID) { type = NavType.StringType })
            ) { backStack ->
                val productId = backStack.arguments?.getString(Routes.ReviewsList.ARG_PRODUCT_ID).orEmpty()

                ReviewsListScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onUserClick = { userId ->
                        navController.navigate(Routes.UserProfile.createRoute(userId))
                    }
                )
            }

            composable(
                route = Routes.UserProfile.route,
                arguments = listOf(navArgument(Routes.UserProfile.ARG_USER_ID) { type = NavType.StringType })
            ) { backStack ->
                val userId = backStack.arguments?.getString(Routes.UserProfile.ARG_USER_ID).orEmpty()

                UserProfileScreen(
                    userId = userId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.NewReview.route,
                arguments = listOf(
                    navArgument(Routes.NewReview.ARG_PRODUCT_ID) {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument(Routes.NewReview.ARG_REVIEW_ID) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStack ->
                val productId = backStack.arguments?.getString(Routes.NewReview.ARG_PRODUCT_ID).orEmpty()
                val reviewId = backStack.arguments?.getString(Routes.NewReview.ARG_REVIEW_ID).orEmpty()

                NewReviewScreen(
                    productId = productId,
                    reviewId = reviewId,
                    onPublicar = { publishedProductId, puntuacion, esEdicion ->
                        if (esEdicion) {
                            // Al editar se vuelve al perfil, donde está la lista de reseñas
                            navController.popBackStack()
                        } else {
                            navController.navigate(
                                Routes.ReviewPublished.createRoute(publishedProductId, puntuacion)
                            ) {
                                popUpTo(Routes.NewReview.route) { inclusive = true }
                            }
                        }
                    },
                    onCancelar = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ReviewPublished.route,
                arguments = listOf(
                    navArgument(Routes.ReviewPublished.ARG_PRODUCT_ID) { type = NavType.StringType },
                    navArgument(Routes.ReviewPublished.ARG_RATING) { type = NavType.IntType }
                )
            ) { backStack ->
                val productId = backStack.arguments?.getString(Routes.ReviewPublished.ARG_PRODUCT_ID).orEmpty()
                val rating = backStack.arguments?.getInt(Routes.ReviewPublished.ARG_RATING) ?: 0

                ReviewPublishedScreen(
                    productId = productId,
                    rating = rating,
                    onCloseClick = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                        }
                    },
                    onVerMiResenaClick = {
                        navController.navigate(Routes.Profile.route) {
                            popUpTo(Routes.Home.route)
                        }
                    },
                    onVolverInicioClick = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
