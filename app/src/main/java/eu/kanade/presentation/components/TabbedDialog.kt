package eu.kanade.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.kyant.shapes.Capsule
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.IconButton
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.MoreHoriz
import tachiyomi.presentation.core.components.material.TabText
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.i18n.stringResource

object TabbedDialogPaddings {
    val Horizontal = 24.dp
    val Vertical = 8.dp
}

@Composable
fun TabbedDialog(
    onDismissRequest: () -> Unit,
    tabTitles: ImmutableList<String>,
    modifier: Modifier = Modifier,
    tabOverflowMenuContent: (@Composable ColumnScope.(() -> Unit) -> Unit)? = null,
    pagerState: PagerState = rememberPagerState { tabTitles.size },
    content: @Composable (Int) -> Unit,
) {
    AdaptiveSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        val scope = rememberCoroutineScope()
        val isGlass = LocalLiquidGlassConfig.current.enabled

        Column(modifier = Modifier.fillMaxWidth()) {
            if (isGlass) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val isDark = isSystemInDarkTheme()
                    val trackBg = if (isDark) Color(0x282C2C2E) else Color(0x38E5E5EA)
                    val pillBg = MaterialTheme.colorScheme.primary
                    val closeBg = if (isDark) Color(0x442C2C2E) else Color(0x55E5E5EA)

                    // Segmented Control Stadium Track with Untinted Liquid Glass
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .liquidGlassSurface(
                                shape = Capsule(),
                                blurRadius = 10.dp,
                                refractionHeight = 14.dp,
                                refractionAmount = 18.dp,
                                tintColor = Color.Unspecified,
                                surfaceColor = Color.Unspecified,
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (isDark) Color(0x35FFFFFF) else Color(0x20000000),
                                shape = Capsule(),
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        val activePage = pagerState.targetPage
                        val segmentWidth = maxWidth / tabTitles.size.coerceAtLeast(1)
                        val indicatorOffset by animateDpAsState(
                            targetValue = segmentWidth * activePage,
                            animationSpec = spring(
                                dampingRatio = 0.85f,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                            label = "SegmentPillOffset",
                        )

                        // Sliding Apple Segmented Pill Indicator matching theme
                        Box(
                            modifier = Modifier
                                .offset(x = indicatorOffset)
                                .width(segmentWidth)
                                .fillMaxHeight()
                                .clip(Capsule())
                                .background(pillBg),
                        )

                        // Segment Text Buttons
                        Row(modifier = Modifier.fillMaxSize()) {
                            tabTitles.fastForEachIndexed { index, title ->
                                val isSelected = activePage == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(Capsule())
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        ),
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                        },
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }

                    // iOS circular close button "X" with Untinted Liquid Glass
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .liquidGlassSurface(
                                shape = CircleShape,
                                blurRadius = 10.dp,
                                refractionHeight = 12.dp,
                                refractionAmount = 16.dp,
                                tintColor = Color.Unspecified,
                                surfaceColor = Color.Unspecified,
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (isDark) Color(0x35FFFFFF) else Color(0x20000000),
                                shape = CircleShape,
                            )
                            .clickable(onClick = onDismissRequest),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(MR.strings.action_close),
                            tint = if (isDark) Color.White.copy(alpha = 0.82f) else Color.Black.copy(alpha = 0.70f),
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    tabOverflowMenuContent?.let { MoreMenu(it) }
                }
            } else {
                Row {
                    PrimaryTabRow(
                        modifier = Modifier.weight(1f),
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        divider = {},
                    ) {
                        tabTitles.fastForEachIndexed { index, tab ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = { TabText(text = tab) },
                                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    tabOverflowMenuContent?.let { MoreMenu(it) }
                }
                HorizontalDivider()
            }

            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                state = pagerState,
                userScrollEnabled = false,
                verticalAlignment = Alignment.Top,
                pageContent = { page -> content(page) },
            )
        }
    }
}

@Composable
private fun MoreMenu(
    content: @Composable ColumnScope.(() -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = stringResource(MR.strings.label_more),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            content { expanded = false }
        }
    }
}
