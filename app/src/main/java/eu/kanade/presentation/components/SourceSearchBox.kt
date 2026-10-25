package eu.kanade.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.shapes.Capsule
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.IconButton
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.clearFocusOnSoftKeyboardHide
import tachiyomi.presentation.core.util.isItemScrollingUp
import tachiyomi.presentation.core.util.runOnEnterKeyPressed
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
fun AnimatedFloatingSearchBox(
    listState: LazyListState,
    searchQuery: String?,
    onChangeSearchQuery: (String?) -> Unit,
    modifier: Modifier = Modifier,
    onGloballyPositioned: (LayoutCoordinates) -> Unit = { },
    placeholderText: String? = null,
    focusManager: FocusManager = LocalFocusManager.current,
    focusRequester: FocusRequester = remember { FocusRequester() },
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) {
    AnimatedVisibility(
        visible = listState.isItemScrollingUp(),
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier,
    ) {
        SourcesSearchBox(
            searchQuery = searchQuery,
            onChangeSearchQuery = onChangeSearchQuery,
            onGloballyPositioned = onGloballyPositioned,
            placeholderText = placeholderText,
            focusManager = focusManager,
            focusRequester = focusRequester,
            keyboardController = keyboardController,
        )
    }
}

@Composable
fun SourcesSearchBox(
    searchQuery: String?,
    onChangeSearchQuery: (String?) -> Unit,
    modifier: Modifier = Modifier,
    onGloballyPositioned: (LayoutCoordinates) -> Unit = { },
    placeholderText: String? = null,
    focusManager: FocusManager = LocalFocusManager.current,
    focusRequester: FocusRequester = remember { FocusRequester() },
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) {
    val searchAndClearFocus: () -> Unit = f@{
        if (searchQuery.isNullOrBlank()) return@f
        focusManager.clearFocus()
        keyboardController?.hide()
    }
    val onClickClearSearch: () -> Unit = {
        onChangeSearchQuery("")
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    val onClickCloseSearch: () -> Unit = {
        onChangeSearchQuery("")
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    var isFocused by remember { mutableStateOf(false) }
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    BasicTextField(
        value = searchQuery ?: "",
        onValueChange = onChangeSearchQuery,
        modifier = modifier
            .onGloballyPositioned(onGloballyPositioned)
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .runOnEnterKeyPressed(action = searchAndClearFocus)
            .clearFocusOnSoftKeyboardHide(),
        enabled = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            lineHeight = 20.sp,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { searchAndClearFocus() }),
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            if (isGlass) {
                val searchBg = if (isDark) {
                    Color(0xFF2C2C2E).copy(alpha = 0.68f)
                } else {
                    Color(0xFFE5E5EA).copy(alpha = 0.76f)
                }
                val isSearching = isFocused || !searchQuery.isNullOrBlank()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .liquidGlassSurface(
                            shape = Capsule(),
                            blurRadius = 10.dp,
                            refractionHeight = 8.dp,
                            refractionAmount = 14.dp,
                            surfaceColor = searchBg,
                            depthEffect = false,
                        )
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isSearching) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onClickCloseSearch),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (searchQuery.isNullOrEmpty()) {
                            Text(
                                modifier = Modifier.secondaryItemAlpha(),
                                text = (placeholderText ?: stringResource(MR.strings.action_search_hint)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                ),
                            )
                        }
                        innerTextField()
                    }
                    if (!searchQuery.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onClickClearSearch),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            } else {
                TextFieldDefaults.DecorationBox(
                    value = searchQuery ?: "",
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    placeholder = {
                        Text(
                            modifier = Modifier.secondaryItemAlpha(),
                            text = (placeholderText ?: stringResource(MR.strings.action_search_hint)),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    leadingIcon = {
                        SearchBoxLeadingIcon(
                            isSearching = isFocused || !searchQuery.isNullOrBlank(),
                            onClickCloseSearch = onClickCloseSearch,
                        )
                    },
                    trailingIcon = {
                        SearchBoxTrailingIcon(
                            isEmpty = searchQuery.isNullOrEmpty(),
                            onClickClearSearch = onClickClearSearch,
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    contentPadding = PaddingValues(MaterialTheme.padding.small),
                )
            }
        },
    )
}

@Composable
fun SearchBoxLeadingIcon(
    isSearching: Boolean,
    modifier: Modifier = Modifier,
    onClickCloseSearch: () -> Unit = {},
) {
    if (isSearching) {
        IconButton(
            modifier = modifier,
            onClick = onClickCloseSearch,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Close",
            )
        }
    } else {
        Icon(
            modifier = modifier,
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
        )
    }
}

@Composable
fun SearchBoxTrailingIcon(
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    onClickClearSearch: () -> Unit = {},
) {
    if (!isEmpty) {
        IconButton(
            modifier = modifier,
            onClick = onClickClearSearch,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Clear",
            )
        }
    }
}

internal val SOURCE_SEARCH_BOX_HEIGHT = 65.dp

@Preview
@Composable
fun PreviewClearFocusOnKeyboardDismissExample() {
    SourcesSearchBox(
        searchQuery = "Hello World",
        onChangeSearchQuery = {},
    )
}
