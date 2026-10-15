package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kyant.shapes.Capsule
import eu.kanade.presentation.category.visualName
import tachiyomi.domain.category.model.Category
import tachiyomi.presentation.core.components.material.LocalInGlassBar
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.TabText
import tachiyomi.presentation.core.components.material.liquidGlassSurface

@Composable
internal fun LibraryTabs(
    categories: List<Category>,
    pagerState: PagerState,
    getItemCountForCategory: (Category) -> Int?,
    onTabItemClick: (Int) -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val currentPageIndex = pagerState.currentPage.coerceAtMost(categories.lastIndex)
    val tabRowModifier = if (isGlass) {
        Modifier
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .liquidGlassSurface(
                shape = Capsule(),
                blurRadius = 10.dp,
                refractionHeight = 12.dp,
                refractionAmount = 18.dp,
            )
    } else {
        Modifier
    }

    Column(modifier = Modifier.zIndex(2f)) {
        androidx.compose.runtime.CompositionLocalProvider(LocalInGlassBar provides isGlass) {
            PrimaryScrollableTabRow(
                modifier = tabRowModifier,
                selectedTabIndex = currentPageIndex,
                edgePadding = if (isGlass) 8.dp else 0.dp,
                containerColor = if (isGlass) Color.Transparent else MaterialTheme.colorScheme.surface,
                divider = {},
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = currentPageIndex == index,
                        onClick = { onTabItemClick(index) },
                        text = {
                            TabText(
                                text = category.visualName,
                                badgeCount = getItemCountForCategory(category),
                            )
                        },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        if (!isGlass) {
            HorizontalDivider()
        }
    }
}
