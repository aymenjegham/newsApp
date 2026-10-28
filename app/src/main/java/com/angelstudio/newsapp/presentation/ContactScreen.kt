package com.angelstudio.newsapp.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.presentation.theme.NewsSpacing
import com.angelstudio.newsapp.presentation.theme.NewsTheme

@Composable
fun ContactScreen(
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val email = stringResource(R.string.jegham_aymen_89_gmail_com)
    val website = stringResource(R.string.contact_website_url)

    BackHandler(onBack = onBack)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = NewsSpacing.page, vertical = NewsSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(NewsSpacing.medium)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
        Text(
            text = stringResource(R.string.contact_brand),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.contact_us),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = stringResource(R.string.aymen_jegham),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.contact_description),
            style = MaterialTheme.typography.bodyLarge
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(NewsSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(NewsSpacing.medium)
            ) {
                Text(
                    text = stringResource(R.string.contact_email_label),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleMedium
                )
                SelectionContainer {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyLarge.merge(
                            TextStyle(textDirection = TextDirection.Ltr)
                        )
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        try {
                            context.startActivity(
                                Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                            )
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                R.string.contact_no_email_app,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {
                    Text(stringResource(R.string.contact_email_action))
                }
            }
        }
        Text(
            text = stringResource(R.string.contact_details_title),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.contact_details),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.contact_support_note),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.contact_website),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge
        )
        TextButton(
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(website)))
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        R.string.contact_no_browser,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        ) {
            Text(
                text = website,
                style = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(textDirection = TextDirection.Ltr)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactScreenPreview() {
    NewsTheme {
        ContactScreen(onBack = {})
    }
}
