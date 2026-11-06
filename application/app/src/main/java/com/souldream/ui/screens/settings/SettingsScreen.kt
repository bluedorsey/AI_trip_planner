package com.souldream.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.souldream.BuildConfig
import com.souldream.R
import com.souldream.ui.animation.pressScale
import com.souldream.ui.components.ScreenTitle
import com.souldream.ui.theme.GlassWhite
import com.souldream.ui.theme.background_colour
import com.souldream.ui.theme.button_colour

/** The signed-in account, shown on the Profile row. */
data class SettingsUiState(val name: String = "", val email: String = "")

data class SettingsActions(val onSignOutClick: () -> Unit = {})

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    actions: SettingsActions,
    modifier: Modifier = Modifier,
    bottomBarHeight: Dp = 0.dp,
) {
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background_colour)
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomBarHeight + navigationBarInset + 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScreenTitle(stringResource(R.string.settings_title))
        SettingRow(
            icon = Icons.Rounded.Person,
            title = state.name.ifBlank { stringResource(R.string.settings_profile) },
            subtitle = state.email.takeIf { it.isNotBlank() },
        )
        SettingRow(
            icon = Icons.Rounded.Info,
            title = stringResource(R.string.settings_about),
            subtitle = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
        )
        SettingRow(
            icon = Icons.AutoMirrored.Rounded.Logout,
            title = stringResource(R.string.settings_sign_out),
            onClick = actions.onSignOutClick,
        )
    }
}

/** A row; it is only tappable when [onClick] is given. */
@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .let { if (onClick != null) it.pressScale(onClick, pressedScale = 0.97f) else it }
            .background(GlassWhite)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = button_colour, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
