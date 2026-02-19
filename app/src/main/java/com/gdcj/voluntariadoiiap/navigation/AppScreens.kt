package com.gdcj.voluntariadoiiap.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen: AppScreens("login_screen")
    object RegisterScreen: AppScreens("register_screen")
    object HomeScreen: AppScreens("home_screen/{name}/{email}") {
        fun createRoute(name: String, email: String) = "home_screen/$name/$email"
    }
}
