package com.angelstudio.newsapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.domain.model.NewsArticle
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Serializable
data object SavedRoute : NavKey

@Serializable
data object SettingsRoute : NavKey

@Serializable
data object ContactRoute : NavKey

@Serializable
data class ArticleRoute(
    val url: String,
    val title: String?,
    val source: String?,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: String?
) : NavKey

@Composable
fun NewsNavigation(viewModel: NewsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigationState = rememberNavigationState(
        startRoute = HomeRoute,
        topLevelRoutes = setOf(HomeRoute, SavedRoute, SettingsRoute)
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val snackbarHostState = remember { SnackbarHostState() }
    val activity = checkNotNull(LocalActivity.current) {
        "NewsNavigation must be hosted by an Activity."
    }
    val refreshErrorMessage = stringResource(R.string.refresh_failed)
    val currentRoute = navigationState.currentRoute
    val isDetailOpen = currentRoute is ArticleRoute || currentRoute == ContactRoute

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = refreshErrorMessage
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isDetailOpen) {
                NewsBottomBar(
                    currentRoute = navigationState.topLevelRoute,
                    onNavigate = navigator::navigate
                )
            }
        }
    ) { padding ->
        val destinations = entryProvider<NavKey> {
            entry<HomeRoute> {
                FeedScreen(
                    state = uiState,
                    onRefresh = viewModel::refresh,
                    onCategorySelected = viewModel::selectCategory,
                    onSave = viewModel::save,
                    onRemove = viewModel::removeSaved,
                    onShare = { article -> shareArticle(article, activity) },
                    onOpenArticle = { article ->
                        article.routeOrNull()?.let(navigator::navigate)
                    },
                    contentPadding = padding
                )
            }
            entry<SavedRoute> {
                SavedScreen(
                    savedArticles = uiState.savedArticles,
                    onRemove = viewModel::removeSaved,
                    onDeleteAll = viewModel::deleteAllSaved,
                    onShare = { article -> shareArticle(article, activity) },
                    onOpenArticle = { article ->
                        article.routeOrNull()?.let { route ->
                            viewModel.markRead(article)
                            navigator.navigate(route)
                        }
                    },
                    contentPadding = padding
                )
            }
            entry<SettingsRoute> {
                SettingsScreen(
                    settings = uiState.settings,
                    onToggleCategory = viewModel::toggleCategory,
                    onSelectCountry = viewModel::selectCountry,
                    onSelectLanguage = viewModel::selectLanguage,
                    onSelectAppearance = viewModel::selectAppearance,
                    onContact = dropUnlessResumed { navigator.navigate(ContactRoute) },
                    contentPadding = padding
                )
            }
            entry<ContactRoute> {
                ContactScreen(onBack = navigator::goBack, contentPadding = padding)
            }
            entry<ArticleRoute> { route ->
                ArticleDetailScreen(
                    route = route,
                    onBack = navigator::goBack,
                    contentPadding = padding
                )
            }
        }

        Box(Modifier.fillMaxSize()) {
            NavDisplay(
                entries = navigationState.toEntries(destinations),
                onBack = navigator::goBack,
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(padding)
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.safeDrawing)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
            )
            if (isDetailOpen) {
                Spacer(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .windowInsetsBottomHeight(WindowInsets.safeDrawing)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                )
            }
        }
    }
}

@Composable
private fun NewsBottomBar(
    currentRoute: NavKey,
    onNavigate: (NavKey) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == HomeRoute,
            onClick = dropUnlessResumed { onNavigate(HomeRoute) },
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.home)) }
        )
        NavigationBarItem(
            selected = currentRoute == SavedRoute,
            onClick = dropUnlessResumed { onNavigate(SavedRoute) },
            icon = { Icon(Icons.Outlined.BookmarkBorder, contentDescription = null) },
            label = { Text(stringResource(R.string.saved)) }
        )
        NavigationBarItem(
            selected = currentRoute == SettingsRoute,
            onClick = dropUnlessResumed { onNavigate(SettingsRoute) },
            icon = { Icon(Icons.Outlined.Tune, contentDescription = null) },
            label = { Text(stringResource(R.string.Settings)) }
        )
    }
}

private fun NewsArticle.routeOrNull(): ArticleRoute? =
    url?.takeIf(String::isNotBlank)?.let {
        ArticleRoute(it, title, sourceName, description, content, imageUrl, publishedAt)
    }

private fun shareArticle(article: NewsArticle, activity: android.app.Activity) {
    val text = activity.getString(
        R.string.share_message,
        article.title.orEmpty(),
        article.url.orEmpty()
    )
    val intent = androidx.core.app.ShareCompat.IntentBuilder(activity)
        .setText(text)
        .setType("text/plain")
        .createChooserIntent()
    activity.startActivity(intent)
}
