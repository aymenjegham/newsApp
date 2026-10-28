package com.angelstudio.newsapp.presentation

import android.content.ActivityNotFoundException
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.presentation.theme.NewsSpacing
import com.angelstudio.newsapp.presentation.theme.NewsTheme

@Composable
fun ArticleDetailScreen(
    route: ArticleRoute,
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    val uriHandler = LocalUriHandler.current
    val articleUri = route.url.toUri()
    val validUrl = articleUri.scheme in setOf("https", "http") && !articleUri.host.isNullOrBlank()
    var browserUnavailable by rememberSaveable(route.url) { mutableStateOf(false) }

    BackHandler(onBack = onBack)
    if (browserUnavailable) {
        AlertDialog(
            onDismissRequest = { browserUnavailable = false },
            text = { Text(stringResource(R.string.browser_unavailable)) },
            confirmButton = {
                TextButton(onClick = { browserUnavailable = false }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = NewsSpacing.page),
        verticalArrangement = Arrangement.spacedBy(NewsSpacing.medium)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = NewsSpacing.small)
                .align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }

        route.imageUrl?.takeIf(String::isNotBlank)?.let { imageUrl ->
            ArticlePhoto(
                imageUrl = imageUrl,
                contentDescription = stringResource(R.string.article_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(216.dp)
                    .padding(bottom = NewsSpacing.small)
            )
        }

        route.source?.takeIf(String::isNotBlank)?.let { source ->
            Text(
                text = source,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = route.title.orEmpty(),
            style = MaterialTheme.typography.headlineMedium
        )
        route.publishedAt?.takeIf(String::isNotBlank)?.let { publishedAt ->
            Text(
                text = articleTimeAgo(publishedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        route.description?.takeIf(String::isNotBlank)?.let { description ->
            Text(text = description, style = MaterialTheme.typography.titleMedium)
        }
        route.content?.takeIf(String::isNotBlank)?.let { content ->
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (validUrl) {
            Button(
                onClick = {
                    try {
                        uriHandler.openUri(route.url)
                    } catch (_: ActivityNotFoundException) {
                        browserUnavailable = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = NewsSpacing.large)
            ) {
                Icon(
                    imageVector = Icons.Outlined.OpenInBrowser,
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.open_article_in_browser),
                    modifier = Modifier.padding(start = NewsSpacing.small),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Text(
                text = stringResource(R.string.article_not_available),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "Article details")
@Composable
private fun ArticleDetailScreenPreview() {
    NewsTheme {
        ArticleDetailScreen(
            route = ArticleRoute(
                url = "https://example.com/article",
                title = "A sample headline for article details",
                source = "The Daily",
                description = "A short summary of this sample article.",
                content = "Read the full article from its original publisher.",
                imageUrl = null,
                publishedAt = null
            ),
            onBack = {}
        )
    }
}
