package eu.kanade.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Close
import tachiyomi.presentation.core.components.material.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import com.kyant.shapes.Capsule
import tachiyomi.presentation.core.components.material.IconButton
import androidx.compose.material3.LocalContentColor
import tachiyomi.presentation.core.components.material.LocalInGlassBar
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.clearFocusOnSoftKeyboardHide
import tachiyomi.presentation.core.util.runOnEnterKeyPressed
import tachiyomi.presentation.core.util.secondaryItemAlpha
import tachiyomi.presentation.core.util.showSoftKeyboard

const val SEARCH_DEBOUNCE_MILLIS = 250L

@Composable
fun AppBar(
    title: String?,

    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    // Text
    subtitle: String? = null,
    // Up button
    navigateUp: (() -> Unit)? = null,
    navigationIcon: ImageVector? = null,
    // Menu
    actions: @Composable RowScope.() -> Unit = {},
    // Action mode
    actionModeCounter: Int = 0,
    onCancelActionMode: () -> Unit = {},
    actionModeActions: @Composable RowScope.() -> Unit = {},

    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val isActionMode by remember(actionModeCounter) {
        derivedStateOf { actionModeCounter > 0 }
    }

    AppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        titleContent = {
            if (isActionMode) {
                AppBarTitle(actionModeCounter.toString())
            } else {
                AppBarTitle(title, subtitle = subtitle)
            }
        },
        navigateUp = navigateUp,
        navigationIcon = navigationIcon,
        actions = {
            if (isActionMode) {
                actionModeActions()
            } else {
                actions()
            }
        },
        isActionMode = isActionMode,
        onCancelActionMode = onCancelActionMode,
        scrollBehavior = scrollBehavior,
    )
}

@Composable
fun AppBar(
    // Title
    titleContent: @Composable () -> Unit,

    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    // Up button
    navigateUp: (() -> Unit)? = null,
    navigationIcon: ImageVector? = null,
    // Menu
    actions: @Composable RowScope.() -> Unit = {},
    // Action mode
    isActionMode: Boolean = false,
    onCancelActionMode: () -> Unit = {},

    // KMK -->
    goHome: (() -> Boolean?)? = null,
    // KMK <--

    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val bgAlpha = backgroundColor?.alpha ?: 1f
    val isAdaptiveTransparent = isGlass && !isActionMode && bgAlpha < 0.05f

    val topAppBarColors = if (isGlass) {
        TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        )
    } else {
        TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor ?: MaterialTheme.colorScheme.surfaceColorAtElevation(
                elevation = if (isActionMode) 3.dp else 0.dp,
            ),
        )
    }
    val appModifier = if (isGlass) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalInGlassBar provides isGlass,
        LocalContentColor provides MaterialTheme.colorScheme.onSurface,
    ) {
        TopAppBar(
            navigationIcon = {
                if (isActionMode) {
                    IconButton(onClick = onCancelActionMode) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(MR.strings.action_cancel),
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        navigateUp?.let {
                            if (isGlass) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 12.dp, end = 4.dp)
                                        .size(40.dp)
                                        .liquidGlassSurface(
                                            shape = CircleShape,
                                            blurRadius = 16.dp,
                                            refractionHeight = 18.dp,
                                            refractionAmount = 26.dp,
                                            tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.75f) else Color.White.copy(alpha = 0.75f),
                                        )
                                        .clickable(onClick = it),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    UpIcon(
                                        navigationIcon = navigationIcon ?: Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            } else {
                                IconButton(onClick = it) {
                                    UpIcon(navigationIcon = navigationIcon)
                                }
                            }
                        }
                        goHome?.let {
                            if (isGlass) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 4.dp, end = 4.dp)
                                        .size(40.dp)
                                        .liquidGlassSurface(
                                            shape = CircleShape,
                                            blurRadius = 16.dp,
                                            refractionHeight = 18.dp,
                                            refractionAmount = 26.dp,
                                            tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.75f) else Color.White.copy(alpha = 0.75f),
                                        )
                                        .clickable(onClick = { it.invoke() }),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    UpIcon(
                                        navigationIcon = Icons.Filled.Home,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            } else {
                                IconButton(onClick = { it.invoke() }) {
                                    UpIcon(navigationIcon = Icons.Filled.Home)
                                }
                            }
                        }
                    }
                }
            },
            title = {
                if (isGlass) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        titleContent()
                    }
                } else {
                    titleContent()
                }
            },
            actions = {
                AppBarActionsWrapper(
                    isGlass = isGlass,
                    isDark = isDark,
                    actions = actions,
                )
            },
            colors = topAppBarColors,
            scrollBehavior = scrollBehavior,
            windowInsets = TopAppBarDefaults.windowInsets,
            modifier = appModifier,
        )
    }
}

@Composable
private fun AppBarActionsWrapper(
    isGlass: Boolean,
    isDark: Boolean,
    actions: @Composable RowScope.() -> Unit,
) {
    if (!isGlass) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            actions()
        }
        return
    }

    SubcomposeLayout { constraints ->
        val placeables = subcompose("content") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions()
            }
        }.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

        val contentWidth = placeables.maxOfOrNull { it.width } ?: 0
        val contentHeight = placeables.maxOfOrNull { it.height } ?: 0

        // If no action items were actually emitted, render nothing (0 width, 0 height)
        if (contentWidth <= 0 || contentHeight <= 0) {
            layout(0, 0) {}
        } else {
            val bgWidth = (contentWidth.toDp() + 8.dp).coerceAtLeast(40.dp)
            val bgHeight = 40.dp
            val backgroundMeasurables = subcompose("background") {
                Box(
                    modifier = Modifier
                        .size(width = bgWidth, height = bgHeight)
                        .liquidGlassSurface(
                            shape = Capsule(),
                            blurRadius = 16.dp,
                            refractionHeight = 18.dp,
                            refractionAmount = 26.dp,
                            tintColor = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.75f) else Color.White.copy(alpha = 0.75f),
                        ),
                )
            }
            val bgPlaceables = backgroundMeasurables.map { it.measure(constraints) }
            val totalContentWidth = maxOf(contentWidth, bgPlaceables.maxOfOrNull { it.width } ?: 0)
            val totalHeight = maxOf(contentHeight, bgPlaceables.maxOfOrNull { it.height } ?: 0)
            val endPaddingPx = 12.dp.roundToPx()
            val totalWidth = totalContentWidth + endPaddingPx

            layout(totalWidth, totalHeight) {
                bgPlaceables.forEach { it.place(0, (totalHeight - it.height) / 2) }
                placeables.forEach { it.place((totalContentWidth - it.width) / 2, (totalHeight - it.height) / 2) }
            }
        }
    }
}

@Composable
fun AppBarTitle(
    title: String?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    Column(
        modifier = modifier,
        horizontalAlignment = if (isGlass) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        title?.let {
            Text(
                text = it,
                style = if (isGlass) {
                    MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                } else {
                    MaterialTheme.typography.titleLarge
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    textAlign = if (isGlass) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(
                    repeatDelayMillis = 2_000,
                ),
            )
        }
    }
}

@Composable
fun AppBarActions(
    actions: ImmutableList<AppBar.AppBarAction>,
) {
    var showMenu by remember { mutableStateOf(false) }
    val isGlass = LocalLiquidGlassConfig.current.enabled

    actions.filterIsInstance<AppBar.Action>().map {
        TooltipBox(
            positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = {
                PlainTooltip {
                    Text(it.title)
                }
            },
            state = rememberTooltipState(),
            focusable = false,
        ) {
            IconButton(
                onClick = it.onClick,
                enabled = it.enabled,
                modifier = if (isGlass) Modifier.size(40.dp) else Modifier,
            ) {
                Icon(
                    imageVector = it.icon,
                    tint = it.iconTint ?: LocalContentColor.current,
                    contentDescription = it.title,
                )
            }
        }
    }

    // KMK -->
    actions.filterIsInstance<AppBar.ActionCompose>().map {
        TooltipBox(
            positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = {
                PlainTooltip {
                    Text(it.title)
                }
            },
            state = rememberTooltipState(),
        ) {
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                it.content()
            }
        }
    }
    // KMK <--

    val overflowActions = actions.filterIsInstance<AppBar.OverflowAction>()
    if (overflowActions.isNotEmpty()) {
        TooltipBox(
            positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = {
                PlainTooltip {
                    Text(stringResource(MR.strings.action_menu_overflow_description))
                }
            },
            state = rememberTooltipState(),
            focusable = false,
        ) {
            IconButton(
                onClick = { showMenu = !showMenu },
                modifier = if (isGlass) Modifier.size(40.dp) else Modifier,
            ) {
                Icon(
                    Icons.Outlined.MoreHoriz,
                    contentDescription = stringResource(MR.strings.action_menu_overflow_description),
                )
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            overflowActions.map {
                DropdownMenuItem(
                    onClick = {
                        it.onClick()
                        showMenu = false
                    },
                    text = { Text(it.title, fontWeight = FontWeight.Normal) },
                )
            }
        }
    }
}

/**
 * @param searchEnabled Set to false if you don't want to show search action.
 * @param searchQuery If null, use normal toolbar.
 * @param placeholderText If null, [MR.strings.action_search_hint] is used.
 */
@Composable
fun SearchToolbar(
    searchQuery: String?,
    onChangeSearchQuery: (String?) -> Unit,
    modifier: Modifier = Modifier,
    titleContent: @Composable () -> Unit = {},
    navigateUp: (() -> Unit)? = null,
    searchEnabled: Boolean = true,
    placeholderText: String? = null,
    onSearch: (String) -> Unit = {},
    onClickCloseSearch: () -> Unit = { onChangeSearchQuery(null) },
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focusRequester = remember { FocusRequester() }

    AppBar(
        modifier = modifier,
        titleContent = {
            if (searchQuery == null) return@AppBar titleContent()

            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current

            val searchAndClearFocus: () -> Unit = f@{
                if (searchQuery.isBlank()) return@f
                onSearch(searchQuery)
                focusManager.clearFocus()
                keyboardController?.hide()
                focusManager.moveFocus(FocusDirection.Next)
            }

            val isGlass = LocalLiquidGlassConfig.current.enabled
            val searchFieldModifier = if (isGlass) {
                Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .liquidGlassSurface(
                        shape = CircleShape,
                        blurRadius = 8.dp,
                        refractionHeight = 10.dp,
                        tintColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
                    )
                    .padding(horizontal = 12.dp)
            } else {
                Modifier.fillMaxWidth()
            }

            BasicTextField(
                value = searchQuery,
                onValueChange = onChangeSearchQuery,
                modifier = searchFieldModifier
                    .focusRequester(focusRequester)
                    .runOnEnterKeyPressed(action = searchAndClearFocus)
                    .showSoftKeyboard(remember { searchQuery.isEmpty() })
                    .clearFocusOnSoftKeyboardHide(),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { searchAndClearFocus() }),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = searchQuery,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = visualTransformation,
                        interactionSource = interactionSource,
                        placeholder = {
                            Text(
                                modifier = Modifier.secondaryItemAlpha(),
                                text = (placeholderText ?: stringResource(MR.strings.action_search_hint)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Normal,
                                ),
                            )
                        },
                        container = {},
                    )
                },
            )
        },
        navigateUp = if (searchQuery == null) navigateUp else onClickCloseSearch,
        actions = {
            key("search") {
                val onClick = { onChangeSearchQuery("") }

                if (!searchEnabled) {
                    // Don't show search action
                } else if (searchQuery == null) {
                    TooltipBox(
                        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(MR.strings.action_search))
                            }
                        },
                        state = rememberTooltipState(),
                        focusable = false,
                    ) {
                        IconButton(
                            onClick = onClick,
                        ) {
                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = stringResource(MR.strings.action_search),
                            )
                        }
                    }
                } else if (searchQuery.isNotEmpty()) {
                    TooltipBox(
                        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(MR.strings.action_reset))
                            }
                        },
                        state = rememberTooltipState(),
                        focusable = false,
                    ) {
                        IconButton(
                            onClick = {
                                onClick()
                                focusRequester.requestFocus()
                            },
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = stringResource(MR.strings.action_reset),
                            )
                        }
                    }
                }
            }

            key("actions") { actions() }
        },
        isActionMode = false,
        scrollBehavior = scrollBehavior,
    )
}

@Composable
fun UpIcon(
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
) {
    val defaultIcon = Icons.AutoMirrored.Outlined.KeyboardArrowLeft
    val icon = navigationIcon ?: defaultIcon
    Icon(
        imageVector = icon,
        contentDescription = stringResource(MR.strings.action_bar_up_description),
        modifier = modifier,
    )
}

sealed interface AppBar {
    sealed interface AppBarAction

    data class Action(
        val title: String,
        val icon: ImageVector,
        val iconTint: Color? = null,
        val onClick: () -> Unit,
        val enabled: Boolean = true,
    ) : AppBarAction

    // KMK -->
    data class ActionCompose(
        val title: String,
        val content: @Composable () -> Unit,
    ) : AppBarAction
    // KMK <--

    data class OverflowAction(
        val title: String,
        val onClick: () -> Unit,
    ) : AppBarAction
}
