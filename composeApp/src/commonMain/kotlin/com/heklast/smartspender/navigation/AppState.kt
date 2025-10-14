package com.heklast.smartspender.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.heklast.smartspender.navigation.Route

class AppState {
    private val _route = MutableStateFlow(Route.Intro)
    val route: StateFlow<Route> = _route

    fun navigate(to: Route) {
        _route.value = to
    }
}