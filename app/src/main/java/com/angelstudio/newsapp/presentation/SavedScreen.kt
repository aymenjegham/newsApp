package com.angelstudio.newsapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.dropUnlessResumed
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.domain.model.NewsArticle
import com.angelstudio.newsapp.domain.model.SavedArticle
import com.angelstudio.newsapp.presentation.theme.NewsSpacing
import com.angelstudio.newsapp.presentation.theme.NewsDimensions
import java.time.Duration
import java.time.Instant

private enum class SavedFilter {
    ALL,
    UNREAD,
    READ
}

@Composable
fun SavedScreen(
    savedArticles: List<SavedArticle>,
    onRemove: (NewsArticle) -> Unit,
    onDeleteAll: () -> Unit,
    onShare: (NewsArticle) -> Unit,
    onOpenArticle: (NewsArticle) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    var selectedFilterName by rememberSaveable { mutableStateOf(SavedFilter.ALL.name) }
    var confirmDeleteAll by rememberSaveable { mutableStateOf(false) }
    val selectedFilter = SavedFilter.valueOf(selectedFilterName)
    val visibleArticles = remember(savedArticles, selectedFilter) {
        savedArticles.filter { saved ->
            when (selectedFilter) {
                SavedFilter.ALL -> true
                SavedFilter.UNREAD -> !saved.isRead
                SavedFilter.READ -> saved.isRead
            }
        }
    }

    if (confirmDeleteAll) {
        AlertDialog(
            onDismissRequest = { confirmDeleteAll = false },
            title = { Text(stringResource(R.string.delete_all_archive)) },
            text = { Text(stringResource(R.string.warning_delete_all)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDeleteAll = false
                    onDeleteAll()
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteAll = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(
            start = NewsSpacing.page,
            end = NewsSpacing.page,
            top = NewsSpacing.medium,
            bottom = NewsSpacing.large
        ),
        verticalArrangement = Arrangement.spacedBy(NewsSpacing.medium)
    ) {
        item(key = "saved-header", contentType = "header") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.saved_count, savedArticles.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.saved),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                if (savedArticles.isNotEmpty()) {
                    IconButton(onClick = { confirmDeleteAll = true }) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = stringResource(R.string.delete_all_archive)
                        )
                    }
                }
            }
        }
        item(key = "saved-filter", contentType = "filter") {
            SavedFilterSelector(
                selected = selectedFilter,
                onSelect = { selectedFilterName = it.name }
            )
        }
        if (visibleArticles.isEmpty()) {
            item(key = "saved-empty", contentType = "empty") {
                Text(
                    text = stringResource(
                        if (savedArticles.isEmpty()) R.string.saved_empty
                        else R.string.saved_empty_filter
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = NewsSpacing.large),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(
                items = visibleArticles,
                key = { it.article.stableKey },
                contentType = { "saved-article" }
            ) { saved ->
                SavedArticleCard(
                    savedArticle = saved,
                    onOpen = dropUnlessResumed { onOpenArticle(saved.article) },
                    onRemove = { onRemove(saved.article) },
                    onShare = { onShare(saved.article) }
                )
            }
        }
    }
}

@Composable
private fun SavedFilterSelector(
    selected: SavedFilter,
    onSelect: (SavedFilter) -> Unit
) {
    val labels = listOf(
        SavedFilter.ALL to stringResource(R.string.all_articles),
        SavedFilter.UNREAD to stringResource(R.string.unread_articles),
        SavedFilter.READ to stringResource(R.string.read_articles)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(NewsDimensions.chipContainerPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEach { (filter, label) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(
                        if (filter == selected) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onSelect(filter) }
                    .padding(vertical = NewsDimensions.chipVerticalPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SavedArticleCard(
    savedArticle: SavedArticle,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
    onShare: () -> Unit
) {
    val article = savedArticle.article
    Card(
        onClick = onOpen,
        enabled = article.url != null,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(NewsSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(NewsSpacing.medium),
            verticalAlignment = Alignment.Top
        ) {
            ArticlePhoto(
                imageUrl = article.imageUrl,
                contentDescription = stringResource(R.string.article_image),
                modifier = Modifier
                    .size(NewsDimensions.savedThumbnail)
                    .clip(MaterialTheme.shapes.small)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.sourceName.orEmpty(),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(R.string.saved_at, savedTimeAgo(savedArticle.savedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = article.title.orEmpty(),
                    modifier = Modifier.padding(top = NewsSpacing.small),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(NewsDimensions.actionIcon)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Bookmark,
                        contentDescription = stringResource(R.string.remove_saved),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(NewsDimensions.actionIcon)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.share_article)
                    )
                }
            }
        }
    }
}

@Composable
private fun savedTimeAgo(savedAt: Long): String {
    val days = Duration.between(Instant.ofEpochMilli(savedAt), Instant.now()).toDays()
        .coerceAtLeast(0)
    return when (days) {
        0L -> stringResource(R.string.today)
        1L -> stringResource(R.string.yesterday)
        else -> stringResource(R.string.days_ago, days.toInt())
    }
}
