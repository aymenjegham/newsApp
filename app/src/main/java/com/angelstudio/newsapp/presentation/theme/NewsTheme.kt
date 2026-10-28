package com.angelstudio.newsapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NewsLightColors = lightColorScheme(
    primary = Color(0xFF4C32D8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE7E1FF),
    onPrimaryContainer = Color(0xFF21105D),
    secondary = Color(0xFF625E6D),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1B1B20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B20),
    surfaceVariant = Color(0xFFF2F1F6),
    onSurfaceVariant = Color(0xFF686773),
    outline = Color(0xFFE6E4EB)
)

private val NewsDarkColors = darkColorScheme(
    primary = Color(0xFFB9A9FF),
    onPrimary = Color(0xFF2D176E),
    primaryContainer = Color(0xFF40268D),
    onPrimaryContainer = Color(0xFFE8DEFF),
    secondary = Color(0xFFC9C3D4),
    onSecondary = Color(0xFF302D38),
    background = Color(0xFF141318),
    onBackground = Color(0xFFE7E1EB),
    surface = Color(0xFF141318),
    onSurface = Color(0xFFE7E1EB),
    surfaceVariant = Color(0xFF292731),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF3A3742)
)

private val NewsTypography = Typography(
    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = Typography().titleLarge.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold)
)

private val NewsShapes = Shapes(
    small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
)

@Composable
fun NewsTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) NewsDarkColors else NewsLightColors
    MaterialTheme(
        colorScheme = colors,
        typography = NewsTypography,
        shapes = NewsShapes,
        content = content
    )
}

object NewsSpacing {
    val page = 20.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val section = 28.dp
}

object NewsDimensions {
    val featuredImageHeight = 216.dp
    val headlineThumbnail = 96.dp
    val savedThumbnail = 74.dp
    val categoryIcon = 18.dp
    val actionIcon = 40.dp
    val chipVerticalPadding = 12.dp
    val chipContainerPadding = 4.dp
    val countryBadgeVerticalPadding = 6.dp
    val countryDialogMaxHeight = 400.dp
}
