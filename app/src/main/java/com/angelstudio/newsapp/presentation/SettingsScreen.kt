package com.angelstudio.newsapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.domain.model.Appearance
import com.angelstudio.newsapp.domain.model.NewsSettings
import com.angelstudio.newsapp.presentation.theme.NewsSpacing
import com.angelstudio.newsapp.presentation.theme.NewsDimensions

private data class CategoryOption(
    val value: String,
    val label: String
)

private data class CountryOption(
    val code: String,
    val label: String
)

@Composable
fun SettingsScreen(
    settings: NewsSettings,
    onToggleCategory: (String) -> Unit,
    onSelectCountry: (String) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onSelectAppearance: (Appearance) -> Unit,
    onContact: () -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    var countryDialogOpen by rememberSaveable { mutableStateOf(false) }
    val categories = listOf(
        CategoryOption("general", stringResource(R.string.general)),
        CategoryOption("business", stringResource(R.string.business)),
        CategoryOption("sports", stringResource(R.string.sports)),
        CategoryOption("health", stringResource(R.string.health)),
        CategoryOption("science", stringResource(R.string.science)),
        CategoryOption("entertainment", stringResource(R.string.entertainment)),
        CategoryOption("technology", stringResource(R.string.technology))
    )
    val countryValues = stringArrayResource(R.array.country_values)
    val countryLabels = stringArrayResource(R.array.country_labels)
    val countries = remember(countryValues, countryLabels) {
        countryValues.mapIndexedNotNull { index, code ->
            countryLabels.getOrNull(index)?.let { label -> CountryOption(code, label) }
        }
    }
    val currentCountry = countries.firstOrNull { it.code == settings.country }

    if (countryDialogOpen) {
        AlertDialog(
            onDismissRequest = { countryDialogOpen = false },
            title = { Text(stringResource(R.string.choose_country)) },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = NewsDimensions.countryDialogMaxHeight),
                    contentPadding = PaddingValues(vertical = NewsSpacing.small)
                ) {
                    items(
                        items = countries,
                        key = { it.code },
                        contentType = { "country" }
                    ) { country ->
                        TextButton(
                            onClick = {
                                countryDialogOpen = false
                                onSelectCountry(country.code)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = country.label,
                                modifier = Modifier.weight(1f)
                            )
                            if (country.code == settings.country) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { countryDialogOpen = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = NewsSpacing.page)
    ) {
        Text(
            text = stringResource(R.string.settings_personalize),
            modifier = Modifier.padding(top = NewsSpacing.medium),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.Settings),
            style = MaterialTheme.typography.headlineLarge
        )

        SettingsSectionTitle(
            text = stringResource(R.string.categories),
            modifier = Modifier.padding(top = NewsSpacing.large)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(NewsSpacing.small),
            verticalArrangement = Arrangement.spacedBy(NewsSpacing.small)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = category.value in settings.categories,
                    onClick = { onToggleCategory(category.value) },
                    label = {
                        Text(
                            text = category.label,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = if (category.value in settings.categories) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(NewsDimensions.categoryIcon)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = NewsSpacing.medium),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(horizontal = NewsSpacing.medium)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { countryDialogOpen = true }
                        .padding(vertical = NewsSpacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = settings.country.uppercase(),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(
                                horizontal = NewsSpacing.small,
                                vertical = NewsDimensions.countryBadgeVerticalPadding
                            ),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = NewsSpacing.medium)
                    ) {
                        Text(
                            text = stringResource(R.string.country),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = currentCountry?.label ?: settings.country.uppercase(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.choose_country),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                androidx.compose.material3.HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = NewsSpacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.news_alerts),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.alerts_unavailable),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = false, onCheckedChange = null, enabled = false)
                }
            }
        }

        SettingsSectionTitle(
            text = stringResource(R.string.language),
            modifier = Modifier.padding(top = NewsSpacing.large)
        )
        ChoicePills(
            labels = listOf("Français", "English", "العربية"),
            selectedIndex = when (settings.language.substringBefore('-')) {
                "fr" -> 0
                "ar", "arab" -> 2
                else -> 1
            },
            onSelect = { index ->
                onSelectLanguage(listOf("fr", "en", "ar")[index])
                val locale = listOf("fr", "en", "ar")[index]
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                    androidx.core.os.LocaleListCompat.forLanguageTags(locale)
                )
            }
        )

        SettingsSectionTitle(
            text = stringResource(R.string.appearance),
            modifier = Modifier.padding(top = NewsSpacing.large)
        )
        ChoicePills(
            labels = listOf(
                stringResource(R.string.appearance_light),
                stringResource(R.string.appearance_dark),
                stringResource(R.string.appearance_system)
            ),
            selectedIndex = when (settings.appearance) {
                Appearance.LIGHT -> 0
                Appearance.DARK -> 1
                Appearance.SYSTEM -> 2
            },
            onSelect = { index ->
                val appearance = listOf(Appearance.LIGHT, Appearance.DARK, Appearance.SYSTEM)[index]
                onSelectAppearance(appearance)
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    when (appearance) {
                        Appearance.LIGHT -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                        Appearance.DARK -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                        Appearance.SYSTEM -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                )
            }
        )

        SettingsSectionTitle(
            text = stringResource(R.string.about),
            modifier = Modifier.padding(top = NewsSpacing.large)
        )
        Text(
            text = stringResource(R.string.about_news_app),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.news_api_credit),
            modifier = Modifier.padding(top = NewsSpacing.small),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onContact) {
            Text(stringResource(R.string.contact_us))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null
            )
        }
        Spacer(Modifier.height(NewsSpacing.large))
    }
}

@Composable
private fun SettingsSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier.padding(bottom = NewsSpacing.small),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ChoicePills(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(NewsDimensions.chipContainerPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEachIndexed { index, label ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(
                        if (index == selectedIndex) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onSelect(index) }
                    .padding(vertical = NewsDimensions.chipVerticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
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
