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

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column

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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        AnimatedVisibility(
            visible = indexing || restoring || syncing || updating,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            IndexingDownloadBanner(
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
            )
        }

        AnimatedVisibility(
            visible = downloadedOnlyMode,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            DownloadedOnlyModeBanner()
        }

        AnimatedVisibility(
            visible = incognitoMode,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            IncognitoModeBanner()
        }
    }
}

@Composable
private fun DownloadedOnlyModeBanner(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val pillBg = if (isDark) Color(0xF01C1C1E) else Color(0xF2FFFFFF)
    val borderColor = if (isDark) Color(0x33FFFFFF) else Color(0x1F000000)
    val accentColor = Color(0xFFFF9F0A)
    val textColor = if (isDark) Color.White else Color(0xFF1C1C1E)

    Row(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = CircleShape)
            .border(width = 0.5.dp, color = borderColor, shape = CircleShape)
            .background(color = pillBg, shape = CircleShape)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.CloudOff,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(15.dp),
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = stringResource(MR.strings.label_downloaded_only),
            color = textColor,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun IncognitoModeBanner(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val pillBg = if (isDark) Color(0xF01C1C1E) else Color(0xF2FFFFFF)
    val borderColor = if (isDark) Color(0x33FFFFFF) else Color(0x1F000000)
    val accentColor = Color(0xFF5E5CE6)
    val textColor = if (isDark) Color.White else Color(0xFF1C1C1E)

    Row(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = CircleShape)
            .border(width = 0.5.dp, color = borderColor, shape = CircleShape)
            .background(color = pillBg, shape = CircleShape)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.VisibilityOff,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(15.dp),
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = stringResource(MR.strings.pref_incognito_mode),
            color = textColor,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun IndexingDownloadBanner(
    modifier: Modifier = Modifier,
    text: String = stringResource(MR.strings.download_notifier_cache_renewal),
) {
    val isDark = isSystemInDarkTheme()
    val pillBg = if (isDark) Color(0xF01C1C1E) else Color(0xF2FFFFFF)
    val borderColor = if (isDark) Color(0x33FFFFFF) else Color(0x1F000000)
    val accentColor = Color(0xFF0A84FF)
    val textColor = if (isDark) Color.White else Color(0xFF1C1C1E)

    Row(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = CircleShape)
            .border(width = 0.5.dp, color = borderColor, shape = CircleShape)
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
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp,
            ),
        )
    }
}
