package com.angelstudio.newsapp.domain.model

data class NewsSettings(
    val categories: Set<String>,
    val country: String,
    val language: String,
    val appearance: Appearance
)

enum class Appearance {
    LIGHT,
    DARK,
    SYSTEM
}
