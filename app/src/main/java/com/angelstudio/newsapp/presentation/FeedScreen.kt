package com.angelstudio.newsapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.dropUnlessResumed
import coil3.compose.AsyncImage
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.domain.model.NewsArticle
import com.angelstudio.newsapp.presentation.theme.NewsDimensions
import com.angelstudio.newsapp.presentation.theme.NewsSpacing
import com.angelstudio.newsapp.presentation.theme.NewsTheme
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val NewsCategories = listOf(
    "general", "business", "sports", "health", "science", "entertainment", "technology"
)

@Preview(showBackground = true, name = "News feed")
@Composable
private fun FeedScreenPreview() {
    NewsTheme {
        FeedScreen(
            state = NewsUiState(
                headlines = listOf(
                    NewsArticle(
                        id = 1,
                        author = "Editorial team",
                        content = null,
                        description = null,
                        publishedAt = Instant.now().toString(),
                        sourceId = null,
                        sourceName = "The Daily",
                        title = "A sample headline for the redesigned news feed",
                        url = "https://example.com/article",
                        imageUrl = null
                    )
                )
            ),
            onRefresh = {},
            onCategorySelected = {},
            onSave = {},
            onRemove = {},
            onShare = {},
            onOpenArticle = {}
        )
    }
}

@Composable
fun FeedScreen(
    state: NewsUiState,
    onRefresh: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onSave: (NewsArticle) -> Unit,
    onRemove: (NewsArticle) -> Unit,
    onShare: (NewsArticle) -> Unit,
    onOpenArticle: (NewsArticle) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredHeadlines = remember(state.headlines, searchQuery) {
        if (searchQuery.isBlank()) {
            state.headlines
        } else {
            state.headlines.filter { it.title.orEmpty().contains(searchQuery, ignoreCase = true) }
        }
    }
    val savedUrls = remember(state.savedArticles) {
        state.savedArticles.mapNotNullTo(mutableSetOf()) { it.article.url }
    }
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                isRefreshing = state.isRefreshing,
                state = pullToRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = contentPadding.calculateTopPadding())
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding + PaddingValues(bottom = NewsSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NewsSpacing.medium)
        ) {
            item(key = "feed-header", contentType = "header") {
                FeedHeader(
                    searchExpanded = searchExpanded,
                    searchQuery = searchQuery,
                    onSearchToggle = {
                        searchExpanded = !searchExpanded
                        if (!searchExpanded) searchQuery = ""
                    },
                    onSearchChange = { searchQuery = it },
                    onRefresh = onRefresh
                )
            }
            item(key = "category-strip", contentType = "category-strip") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = NewsSpacing.page),
                    horizontalArrangement = Arrangement.spacedBy(NewsSpacing.small)
                ) {
                    items(
                        items = NewsCategories,
                        key = { it },
                        contentType = { "category-chip" }
                    ) { category ->
                        FilterChip(
                            selected = category in state.settings.categories,
                            onClick = { onCategorySelected(category) },
                            label = { Text(categoryLabel(category)) }
                        )
                    }
                }
            }

            if (filteredHeadlines.isEmpty()) {
                item(key = "empty-feed", contentType = "empty") {
                    EmptyFeed(
                        isLoading = state.isRefreshing,
                        isSearching = searchQuery.isNotBlank()
                    )
                }
            } else {
                val featured = filteredHeadlines.first()
                item(key = "featured-${featured.stableKey}", contentType = "featured") {
                    FeaturedArticle(
                        article = featured,
                        isSaved = featured.url in savedUrls,
                        onOpen = dropUnlessResumed { onOpenArticle(featured) },
                        onSave = {
                            if (featured.url in savedUrls) onRemove(featured)
                            else onSave(featured)
                        },
                        onShare = { onShare(featured) }
                    )
                }
                items(
                    items = filteredHeadlines.drop(1),
                    key = { it.stableKey },
                    contentType = { "headline-row" }
                ) { article ->
                    HeadlineRow(
                        article = article,
                        isSaved = article.url in savedUrls,
                        onOpen = dropUnlessResumed { onOpenArticle(article) },
                        onSave = {
                            if (article.url in savedUrls) onRemove(article)
                            else onSave(article)
                        },
                        onShare = { onShare(article) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedHeader(
    searchExpanded: Boolean,
    searchQuery: String,
    onSearchToggle: () -> Unit,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val dateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("EEEE, d MMMM", locale)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = NewsSpacing.page, end = NewsSpacing.page, top = NewsSpacing.medium)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateFormatter.format(java.time.LocalDate.now()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onSearchToggle) {
                Icon(
                    imageVector = if (searchExpanded) Icons.Outlined.Close else Icons.Outlined.Search,
                    contentDescription = stringResource(
                        if (searchExpanded) R.string.close_search else R.string.show_search
                    )
                )
            }
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }
        if (searchExpanded) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = NewsSpacing.small),
                singleLine = true,
                label = { Text(stringResource(R.string.search_placeholder)) }
            )
        }
    }
}

@Composable
private fun FeaturedArticle(
    article: NewsArticle,
    isSaved: Boolean,
    onOpen: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NewsSpacing.page)
    ) {
        ArticlePhoto(
            imageUrl = article.imageUrl,
            contentDescription = stringResource(R.string.article_image),
            modifier = Modifier
                .fillMaxWidth()
                .height(NewsDimensions.featuredImageHeight)
                .clickable(enabled = article.url != null, onClick = onOpen)
        )
        Spacer(Modifier.height(NewsSpacing.small))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = article.sourceName.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "  ·  ${articleTimeAgo(article.publishedAt)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = article.title.orEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = article.url != null, onClick = onOpen)
                .padding(top = NewsSpacing.small),
            style = MaterialTheme.typography.headlineMedium,
        )
        article.author?.takeIf(String::isNotBlank)?.let { author ->
            Text(
                text = author,
                modifier = Modifier.padding(top = NewsSpacing.small),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ArticleActions(
            isSaved = isSaved,
            onSave = onSave,
            onShare = onShare,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun HeadlineRow(
    article: NewsArticle,
    isSaved: Boolean,
    onOpen: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = article.url != null, onClick = onOpen)
                .padding(horizontal = NewsSpacing.page, vertical = NewsSpacing.small),
            horizontalArrangement = Arrangement.spacedBy(NewsSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.sourceName.orEmpty(),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = articleTimeAgo(article.publishedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = article.title.orEmpty(),
                    modifier = Modifier.padding(top = NewsSpacing.small),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
            ArticlePhoto(
                imageUrl = article.imageUrl,
                contentDescription = stringResource(R.string.article_image),
                modifier = Modifier
                    .size(NewsDimensions.headlineThumbnail)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(enabled = article.url != null, onClick = onOpen)
            )
        }
        ArticleActions(
            isSaved = isSaved,
            onSave = onSave,
            onShare = onShare,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = NewsSpacing.page)
        )
        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(start = NewsSpacing.page, end = NewsSpacing.page)
        )
    }
}

@Composable
private fun ArticleActions(
    isSaved: Boolean,
    onSave: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onSave) {
            Icon(
                imageVector = if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = stringResource(
                    if (isSaved) R.string.remove_saved else R.string.read_later
                ),
                tint = if (isSaved) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onShare) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = stringResource(R.string.share_article)
            )
        }
    }
}

@Composable
fun ArticlePhoto(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer)
    )
}

@Composable
private fun EmptyFeed(isLoading: Boolean, isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NewsSpacing.page, vertical = NewsSpacing.section),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = stringResource(
                    if (isSearching) R.string.no_search_results else R.string.no_headlines
                ),
                style = MaterialTheme.typography.titleLarge
            )
            if (!isSearching) {
                Text(
                    text = stringResource(R.string.no_headlines_hint),
                    modifier = Modifier.padding(top = NewsSpacing.small),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun categoryLabel(category: String): String {
    val label = when (category) {
        "general" -> R.string.general
        "business" -> R.string.business
        "sports" -> R.string.sports
        "health" -> R.string.health
        "science" -> R.string.science
        "entertainment" -> R.string.entertainment
        else -> R.string.technology
    }
    return stringResource(label)
}

@Composable
fun articleTimeAgo(publishedAt: String?): String {
    val publishedInstant = remember(publishedAt) {
        try {
            publishedAt?.let(Instant::parse)
        } catch (_: DateTimeParseException) {
            null
        }
    }
    val minutes = publishedInstant?.let { Duration.between(it, Instant.now()).toMinutes() }
        ?.coerceAtLeast(0)
    return when {
        minutes == null -> publishedAt.orEmpty()
        minutes < 1 -> stringResource(R.string.just_now)
        minutes < 60 -> stringResource(R.string.minutes_ago, minutes.toInt())
        minutes < 1_440 -> stringResource(R.string.hours_ago, (minutes / 60).toInt())
        else -> stringResource(R.string.days_time_ago, (minutes / 1_440).toInt())
    }
}
