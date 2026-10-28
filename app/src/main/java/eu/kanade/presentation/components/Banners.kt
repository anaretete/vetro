package eu.kanade.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import dev.icerock.moko.resources.StringResource
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import java.math.RoundingMode
import java.text.NumberFormat

val DownloadedOnlyBannerBackgroundColor
    @Composable get() = Color(0xFFFF9F0A)
val IncognitoModeBannerBackgroundColor
    @Composable get() = Color(0xFF5E5CE6)
val IndexingBannerBackgroundColor
    @Composable get() = Color(0xFF0A84FF)

// KMK -->
val RestoringBannerBackgroundColor
    @Composable get() = MaterialTheme.colorScheme.error
val SyncingBannerBackgroundColor
    @Composable get() = MaterialTheme.colorScheme.secondary
val UpdatingBannerBackgroundColor
    @Composable get() = MaterialTheme.colorScheme.tertiary
// KMK <--

@Composable
fun WarningBanner(
    textRes: StringResource,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(textRes),
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.error)
            .padding(16.dp),
        color = MaterialTheme.colorScheme.onError,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
    )
}

// KMK -->
private val percentFormatter = NumberFormat.getPercentInstance().apply {
    roundingMode = RoundingMode.DOWN
    maximumFractionDigits = 0
}
// KMK <--

@Composable
fun AppStateBanners(
    downloadedOnlyMode: Boolean,
    incognitoMode: Boolean,
    indexing: Boolean,
    // KMK -->
    restoring: Boolean,
    syncing: Boolean,
    updating: Boolean,
    // KMK <--
    modifier: Modifier = Modifier,
    // KMK -->
    progress: Float? = null,
    // KMK <--
) {
    val density = LocalDensity.current
    val mainInsets = WindowInsets.statusBars
    val mainInsetsTop = mainInsets.getTop(density)
    SubcomposeLayout(modifier = modifier) { constraints ->
        val indexingPlaceable = subcompose(0) {
            AnimatedVisibility(
                // KMK -->
                visible = indexing || restoring || syncing || updating,
                // KMK <--
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                IndexingDownloadBanner(
                    modifier = Modifier.windowInsetsPadding(mainInsets),
                    // KMK -->
                    text = when {
                        updating -> progress?.let {
                            stringResource(
                                MR.strings.notification_updating_progress,
                                percentFormatter.format(it),
                            )
                        } ?: stringResource(MR.strings.updating_library)
                        syncing -> progress?.let {
                            stringResource(MR.strings.syncing_library) + " (${percentFormatter.format(it)})"
                        } ?: stringResource(MR.strings.syncing_library)
                        restoring -> progress?.let {
                            stringResource(MR.strings.restoring_backup) + " (${percentFormatter.format(it)})"
                        } ?: stringResource(MR.strings.restoring_backup)
                        else -> stringResource(MR.strings.download_notifier_cache_renewal)
                    },
                    // KMK <--
                )
            }
        }.fastMap { it.measure(constraints) }
        val indexingHeight = indexingPlaceable.fastMaxBy { it.height }?.height ?: 0

        val downloadedOnlyPlaceable = subcompose(1) {
            AnimatedVisibility(
                visible = downloadedOnlyMode,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                val top = (mainInsetsTop - indexingHeight).coerceAtLeast(0)
                DownloadedOnlyModeBanner(
                    modifier = Modifier.windowInsetsPadding(WindowInsets(top = top)),
                )
            }
        }.fastMap { it.measure(constraints) }
        val downloadedOnlyHeight = downloadedOnlyPlaceable.fastMaxBy { it.height }?.height ?: 0

        val incognitoPlaceable = subcompose(2) {
            AnimatedVisibility(
                visible = incognitoMode,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                val top = (mainInsetsTop - indexingHeight - downloadedOnlyHeight).coerceAtLeast(0)
                IncognitoModeBanner(
                    modifier = Modifier.windowInsetsPadding(WindowInsets(top = top)),
                )
            }
        }.fastMap { it.measure(constraints) }
        val incognitoHeight = incognitoPlaceable.fastMaxBy { it.height }?.height ?: 0

        layout(constraints.maxWidth, indexingHeight + downloadedOnlyHeight + incognitoHeight) {
            indexingPlaceable.fastForEach {
                it.place(0, 0)
            }
            downloadedOnlyPlaceable.fastForEach {
                it.place(0, indexingHeight)
            }
            incognitoPlaceable.fastForEach {
                it.place(0, indexingHeight + downloadedOnlyHeight)
            }
        }
    }
}

@Composable
private fun DownloadedOnlyModeBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        val isDark = isSystemInDarkTheme()
        val pillBg = if (isDark) Color(0xD92C2010) else Color(0xF2FFF8E1)
        val accentColor = Color(0xFFFF9F0A)
        val borderColor = accentColor.copy(alpha = if (isDark) 0.35f else 0.45f)
        val textColor = if (isDark) Color(0xFFFFD6A5) else Color(0xFF8A4B00)

        Row(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = CircleShape)
                .border(width = 1.dp, color = borderColor, shape = CircleShape)
                .background(color = pillBg, shape = CircleShape)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(color = accentColor, shape = CircleShape),
            )
            Spacer(modifier = Modifier.width(7.dp))
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(15.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(MR.strings.label_downloaded_only),
                color = textColor,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun IncognitoModeBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        val isDark = isSystemInDarkTheme()
        val pillBg = if (isDark) Color(0xD91E1A33) else Color(0xF2F0EFFF)
        val accentColor = Color(0xFF5E5CE6)
        val borderColor = accentColor.copy(alpha = if (isDark) 0.35f else 0.45f)
        val textColor = if (isDark) Color(0xFFD6D4FF) else Color(0xFF383594)

        Row(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = CircleShape)
                .border(width = 1.dp, color = borderColor, shape = CircleShape)
                .background(color = pillBg, shape = CircleShape)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(color = accentColor, shape = CircleShape),
            )
            Spacer(modifier = Modifier.width(7.dp))
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(15.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(MR.strings.pref_incognito_mode),
                color = textColor,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun IndexingDownloadBanner(
    modifier: Modifier = Modifier,
    // KMK -->
    text: String = stringResource(MR.strings.download_notifier_cache_renewal),
    // KMK <--
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        val isDark = isSystemInDarkTheme()
        val pillBg = if (isDark) Color(0xD91C2533) else Color(0xF2EBF5FF)
        val accentColor = Color(0xFF0A84FF)
        val borderColor = accentColor.copy(alpha = if (isDark) 0.35f else 0.45f)
        val textColor = if (isDark) Color(0xFFBCE0FF) else Color(0xFF004F99)

        Row(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = CircleShape)
                .border(width = 1.dp, color = borderColor, shape = CircleShape)
                .background(color = pillBg, shape = CircleShape)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(13.dp),
                color = accentColor,
                strokeWidth = 2.dp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                // KMK -->
                text = text,
                // KMK <--
                color = textColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp,
                ),
            )
        }
    }
}
