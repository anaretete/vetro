package eu.kanade.presentation.more.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle
import tachiyomi.presentation.core.components.material.CupertinoIconBadge
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ChromeReaderMode
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.GetApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.StringResource
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.more.settings.screen.about.AboutScreen
import eu.kanade.presentation.more.settings.widget.TextPreferenceWidget
import eu.kanade.presentation.util.LocalBackPress
import eu.kanade.presentation.util.Screen
import exh.assets.EhAssets
import exh.assets.ehassets.EhLogo
import exh.assets.ehassets.MangadexLogo
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import cafe.adriel.voyager.core.screen.Screen as VoyagerScreen

object SettingsMainScreen : Screen() {
    @Suppress("unused")
    private fun readResolve(): Any = SettingsMainScreen

    @Composable
    override fun Content() {
        Content(twoPane = false)
    }

    @Composable
    private fun getPalerSurface(): Color {
        val surface = MaterialTheme.colorScheme.surface
        val dark = isSystemInDarkTheme()
        return remember(surface, dark) {
            val arr = FloatArray(3)
            ColorUtils.colorToHSL(surface.toArgb(), arr)
            arr[2] = if (dark) {
                arr[2] - 0.05f
            } else {
                arr[2] + 0.02f
            }.coerceIn(0f, 1f)
            Color.hsl(arr[0], arr[1], arr[2])
        }
    }

    @Composable
    fun Content(twoPane: Boolean) {
        val navigator = LocalNavigator.currentOrThrow
        val backPress = LocalBackPress.currentOrThrow
        val isGlass = LocalLiquidGlassConfig.current.enabled
        val isDark = isSystemInDarkTheme()
        val containerColor = if (isGlass) {
            if (isDark) Color(0xFF000000) else Color(0xFFF2F2F7)
        } else if (twoPane) {
            getPalerSurface()
        } else {
            MaterialTheme.colorScheme.surface
        }
        val topBarState = rememberTopAppBarState()

        Scaffold(
            topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState),
            topBar = { scrollBehavior ->
                AppBar(
                    title = stringResource(MR.strings.label_settings),
                    navigateUp = backPress::invoke,
                    actions = {
                        AppBarActions(
                            persistentListOf(
                                AppBar.Action(
                                    title = stringResource(MR.strings.action_search),
                                    icon = Icons.Outlined.Search,
                                    onClick = { navigator.navigate(SettingsSearchScreen(), twoPane) },
                                ),
                            ),
                        )
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            containerColor = containerColor,
            content = { contentPadding ->
                val state = rememberLazyListState()
                // SY -->
                val items = allItems.filter { it.screen !is SearchableSettings || it.screen.isEnabled() }
                val filteredGroups = groups.map { grp ->
                    grp.filter { it.screen !is SearchableSettings || it.screen.isEnabled() }
                }.filter { it.isNotEmpty() }
                // SY <--
                val indexSelected = if (twoPane) {
                    items.indexOfFirst { it.screen::class == navigator.items.first()::class }
                        .also {
                            LaunchedEffect(Unit) {
                                state.animateScrollToItem(it)
                                if (it > 0) {
                                    // Lift scroll
                                    topBarState.contentOffset = topBarState.heightOffsetLimit
                                }
                            }
                        }
                } else {
                    null
                }

                LazyColumn(
                    state = state,
                    contentPadding = contentPadding,
                ) {
                    if (isGlass) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        filteredGroups.forEach { cardItems ->
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .clip(RoundedRectangle(20.dp, RoundedCornerStyle.Continuous))
                                        .background(if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)),
                                ) {
                                    Column {
                                        cardItems.forEachIndexed { itemIndex, item ->
                                            val selected = indexSelected == items.indexOf(item)
                                            var rowModifier = Modifier.fillMaxWidth()
                                            if (twoPane && selected) {
                                                rowModifier = rowModifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                            }
                                            TextPreferenceWidget(
                                                modifier = rowModifier,
                                                title = item.formatTitle(),
                                                subtitle = item.formatSubtitle(),
                                                iconContent = {
                                                    CupertinoIconBadge(
                                                        icon = item.icon,
                                                        backgroundColor = item.badgeColor,
                                                    )
                                                },
                                                onPreferenceClick = { navigator.navigate(item.screen, twoPane) },
                                            )
                                            if (itemIndex < cardItems.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = 58.dp),
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
                    } else {
                        itemsIndexed(
                            items = items,
                            key = { _, item -> "settings-main-${item.hashCode()}" },
                        ) { index, item ->
                            val selected = indexSelected == index
                            var modifier: Modifier = Modifier
                            var contentColor = LocalContentColor.current
                            if (twoPane) {
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .then(
                                        if (selected) {
                                            Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                        } else {
                                            Modifier
                                        },
                                    )
                                if (selected) {
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            }
                            CompositionLocalProvider(LocalContentColor provides contentColor) {
                                TextPreferenceWidget(
                                    modifier = modifier,
                                    title = item.formatTitle(),
                                    subtitle = item.formatSubtitle(),
                                    icon = item.icon,
                                    onPreferenceClick = { navigator.navigate(item.screen, twoPane) },
                                )
                            }
                        }
                    }
                }
            },
        )
    }

    private fun Navigator.navigate(screen: VoyagerScreen, twoPane: Boolean) {
        if (twoPane) replaceAll(screen) else push(screen)
    }

    private data class Item(
        val titleRes: StringResource? = null,
        val subtitleRes: StringResource? = null,
        val customTitle: String? = null,
        val customSubtitle: String? = null,
        val formatTitle: @Composable () -> String = { customTitle ?: titleRes?.let { stringResource(it) } ?: "" },
        val formatSubtitle: @Composable () -> String = { customSubtitle ?: subtitleRes?.let { stringResource(it) } ?: "" },
        val icon: ImageVector,
        val badgeColor: Color,
        val screen: VoyagerScreen,
    )

    private val group1 = listOf(
        Item(
            customTitle = "Liquid Glass",
            customSubtitle = "Refraction, blur, lenses, and physics",
            icon = Icons.Outlined.AutoAwesome,
            badgeColor = Color(0xFF30B0C7),
            screen = SettingsLiquidGlassScreen,
        ),
        Item(
            titleRes = MR.strings.pref_category_appearance,
            subtitleRes = MR.strings.pref_appearance_summary,
            icon = Icons.Outlined.Palette,
            badgeColor = Color(0xFF5856D6),
            screen = SettingsAppearanceScreen,
        ),
        Item(
            titleRes = MR.strings.pref_category_library,
            subtitleRes = MR.strings.pref_library_summary,
            icon = Icons.Outlined.CollectionsBookmark,
            badgeColor = Color(0xFF007AFF),
            screen = SettingsLibraryScreen,
        ),
        Item(
            titleRes = MR.strings.pref_category_reader,
            subtitleRes = MR.strings.pref_reader_summary,
            icon = Icons.AutoMirrored.Outlined.ChromeReaderMode,
            badgeColor = Color(0xFFFF9500),
            screen = SettingsReaderScreen,
        ),
        Item(
            titleRes = MR.strings.pref_category_downloads,
            subtitleRes = MR.strings.pref_downloads_summary,
            icon = Icons.Outlined.GetApp,
            badgeColor = Color(0xFF34C759),
            screen = SettingsDownloadScreen,
        ),
    )

    private val group2 = listOf(
        Item(
            titleRes = MR.strings.pref_category_tracking,
            subtitleRes = MR.strings.pref_tracking_summary,
            icon = Icons.Outlined.Sync,
            badgeColor = Color(0xFF30B0C7),
            screen = SettingsTrackingScreen,
        ),
        // AM (CONNECTIONS) -->
        Item(
            titleRes = KMR.strings.pref_category_connections,
            subtitleRes = KMR.strings.pref_connections_summary,
            icon = Icons.Outlined.Link,
            badgeColor = Color(0xFF5AC8FA),
            screen = SettingsConnectionScreen,
        ),
        // <-- AM (CONNECTIONS)
        Item(
            titleRes = MR.strings.browse,
            subtitleRes = MR.strings.pref_browse_summary,
            icon = Icons.Outlined.Explore,
            badgeColor = Color(0xFF0A84FF),
            screen = SettingsBrowseScreen,
        ),
        // SY -->
        Item(
            titleRes = SYMR.strings.pref_category_eh,
            subtitleRes = SYMR.strings.pref_ehentai_summary,
            icon = EhAssets.EhLogo,
            badgeColor = Color(0xFFFF2D55),
            screen = SettingsEhScreen,
        ),
        Item(
            titleRes = SYMR.strings.pref_category_mangadex,
            subtitleRes = SYMR.strings.pref_mangadex_summary,
            icon = EhAssets.MangadexLogo,
            badgeColor = Color(0xFFFF9500),
            screen = SettingsMangadexScreen,
        ),
        // SY <--
    )

    private val group3 = listOf(
        Item(
            titleRes = MR.strings.label_data_storage,
            subtitleRes = MR.strings.pref_backup_summary,
            icon = Icons.Outlined.Storage,
            badgeColor = Color(0xFFAF52DE),
            screen = SettingsDataScreen,
        ),
        Item(
            titleRes = MR.strings.pref_category_security,
            subtitleRes = MR.strings.pref_security_summary,
            icon = Icons.Outlined.Security,
            badgeColor = Color(0xFFFF3B30),
            screen = SettingsSecurityScreen,
        ),
    )

    private val group4 = listOf(
        Item(
            titleRes = MR.strings.pref_category_advanced,
            subtitleRes = MR.strings.pref_advanced_summary,
            icon = Icons.Outlined.Code,
            badgeColor = Color(0xFF8E8E93),
            screen = SettingsAdvancedScreen,
        ),
        Item(
            titleRes = MR.strings.pref_category_about,
            subtitleRes = StringResource(0),
            formatSubtitle = {
                "${stringResource(MR.strings.app_name)} ${AboutScreen.getVersionName(withBuildDate = false)}"
            },
            icon = Icons.Outlined.Info,
            badgeColor = Color(0xFF8E8E93),
            screen = AboutScreen(),
        ),
    )

    private val groups = listOf(group1, group2, group3, group4)
    private val allItems = groups.flatten()
}
