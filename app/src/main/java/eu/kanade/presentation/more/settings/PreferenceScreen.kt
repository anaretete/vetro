package eu.kanade.presentation.more.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle
import eu.kanade.presentation.more.settings.screen.SearchableSettings
import eu.kanade.presentation.more.settings.widget.PreferenceGroupHeader
import kotlinx.coroutines.delay
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import kotlin.time.Duration.Companion.seconds

private sealed interface PreferenceGroupChunk {
    data class Group(val group: Preference.PreferenceGroup) : PreferenceGroupChunk
    data class Standalone(val items: List<Preference.PreferenceItem<*, *>>) : PreferenceGroupChunk
}

/**
 * Preference Screen composable which contains a list of [Preference] items
 * @param items [Preference] items which should be displayed on the preference screen. An item can be a single [PreferenceItem] or a group ([Preference.PreferenceGroup])
 * @param modifier [Modifier] to be applied to the preferenceScreen layout
 */
@Composable
fun PreferenceScreen(
    items: List<Preference>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val state = rememberLazyListState()
    val highlightKey = SearchableSettings.highlightKey
    if (highlightKey != null) {
        LaunchedEffect(Unit) {
            val i = items.findHighlightedIndex(highlightKey)
            if (i >= 0) {
                delay(0.5.seconds)
                state.animateScrollToItem(i)
            }
            SearchableSettings.highlightKey = null
        }
    }

    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    if (isGlass) {
        CompositionLocalProvider(
            LocalPreferenceMinHeight provides 48.dp,
        ) {
            val chunks = buildList {
                var standaloneBuffer = mutableListOf<Preference.PreferenceItem<*, *>>()
                for (pref in items) {
                    when (pref) {
                        is Preference.PreferenceGroup -> {
                            if (pref.enabled) {
                                if (standaloneBuffer.isNotEmpty()) {
                                    add(PreferenceGroupChunk.Standalone(standaloneBuffer.toList()))
                                    standaloneBuffer = mutableListOf()
                                }
                                add(PreferenceGroupChunk.Group(pref))
                            }
                        }
                        is Preference.PreferenceItem<*, *> -> {
                            standaloneBuffer.add(pref)
                        }
                    }
                }
                if (standaloneBuffer.isNotEmpty()) {
                    add(PreferenceGroupChunk.Standalone(standaloneBuffer.toList()))
                }
            }

            ScrollbarLazyColumn(
                modifier = modifier,
                state = state,
                contentPadding = contentPadding,
            ) {
                chunks.forEach { chunk ->
                    when (chunk) {
                        is PreferenceGroupChunk.Group -> {
                            if (chunk.group.title.isNotBlank()) {
                                item {
                                    PreferenceGroupHeader(title = chunk.group.title)
                                }
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .clip(RoundedRectangle(20.dp, RoundedCornerStyle.Continuous))
                                        .background(if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)),
                                ) {
                                    Column {
                                        chunk.group.preferenceItems.forEachIndexed { itemIndex, item ->
                                            PreferenceItem(
                                                item = item,
                                                highlightKey = highlightKey,
                                            )
                                            if (itemIndex < chunk.group.preferenceItems.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(
                                                        start = if (item.icon != null) 54.dp else 16.dp,
                                                    ),
                                                    thickness = 0.5.dp,
                                                    color = if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        is PreferenceGroupChunk.Standalone -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .clip(RoundedRectangle(20.dp, RoundedCornerStyle.Continuous))
                                        .background(if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)),
                                ) {
                                    Column {
                                        chunk.items.forEachIndexed { itemIndex, item ->
                                            PreferenceItem(
                                                item = item,
                                                highlightKey = highlightKey,
                                            )
                                            if (itemIndex < chunk.items.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(
                                                        start = if (item.icon != null) 54.dp else 16.dp,
                                                    ),
                                                    thickness = 0.5.dp,
                                                    color = if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    } else {
        ScrollbarLazyColumn(
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
        ) {
            items.fastForEachIndexed { i, preference ->
                when (preference) {
                    // Create Preference Group
                    is Preference.PreferenceGroup -> {
                        if (!preference.enabled) return@fastForEachIndexed

                        item {
                            Column {
                                PreferenceGroupHeader(title = preference.title)
                            }
                        }
                        items(preference.preferenceItems) { item ->
                            PreferenceItem(
                                item = item,
                                highlightKey = highlightKey,
                            )
                        }
                        item {
                            if (i < items.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    // Create Preference Item
                    is Preference.PreferenceItem<*, *> -> item {
                        PreferenceItem(
                            item = preference,
                            highlightKey = highlightKey,
                        )
                    }
                }
            }
        }
    }
}

private fun List<Preference>.findHighlightedIndex(highlightKey: String): Int {
    return flatMap {
        if (it is Preference.PreferenceGroup) {
            buildList<String?> {
                add(null) // Header
                addAll(it.preferenceItems.map { groupItem -> groupItem.title })
                add(null) // Spacer
            }
        } else {
            listOf(it.title)
        }
    }.indexOfFirst { it == highlightKey }
}
