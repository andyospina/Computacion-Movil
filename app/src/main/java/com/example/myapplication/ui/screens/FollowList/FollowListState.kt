package com.example.myapplication.ui.screens.FollowList

import androidx.annotation.StringRes
import com.example.myapplication.data.User

enum class FollowTab(val routeValue: String) {
    SEGUIDORES("followers"),
    SIGUIENDO("following");

    companion object {
        fun fromRoute(value: String?): FollowTab = entries.firstOrNull { it.routeValue == value } ?: SEGUIDORES
    }
}

data class FollowListState(
    val userId: String = "",
    val tab: FollowTab = FollowTab.SEGUIDORES,
    val seguidores: List<User> = emptyList(),
    val seguidos: List<User> = emptyList(),
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null
)
