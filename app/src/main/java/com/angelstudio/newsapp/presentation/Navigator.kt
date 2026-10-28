package com.angelstudio.newsapp.presentation

import androidx.navigation3.runtime.NavKey

class Navigator(private val state: NavigationState) {
    fun navigate(route: NavKey) {
        if (route in state.backStacks) {
            state.topLevelRoute = route
        } else {
            state.currentBackStack.add(route)
        }
    }

    fun goBack() {
        val currentBackStack = state.currentBackStack
        if (currentBackStack.size > 1) {
            currentBackStack.removeLastOrNull()
        } else if (state.topLevelRoute != state.startRoute) {
            state.topLevelRoute = state.startRoute
        }
    }
}
