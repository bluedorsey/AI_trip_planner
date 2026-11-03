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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.ui.animation.pressScale
import com.souldream.ui.components.ScreenTitle
import com.souldream.ui.theme.GlassWhite
import com.souldream.ui.theme.background_colour
import com.souldream.ui.theme.button_colour

data class SettingsUiState(val notificationsEnabled: Boolean = true)

data class SettingsActions(
    val onProfileClick: () -> Unit = {},
    val onNotificationsChange: (Boolean) -> Unit = {},
    val onAboutClick: () -> Unit = {},
    val onSignOutClick: () -> Unit = {},
)

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    actions: SettingsActions,
    modifier: Modifier = Modifier,
    bottomBarHeight: Dp = 0.dp,
) {
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // Held locally so the switch responds before a data layer persists it.
    var notifications by rememberSaveable { mutableStateOf(state.notificationsEnabled) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background_colour)
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomBarHeight + navigationBarInset + 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScreenTitle(stringResource(R.string.settings_title))
        SettingRow(Icons.Rounded.Person, stringResource(R.string.settings_profile), actions.onProfileClick) { Chevron() }
        SettingRow(Icons.Rounded.Notifications, stringResource(R.string.settings_notifications), null) {
            Switch(
                checked = notifications,
                onCheckedChange = {
                    notifications = it
                    actions.onNotificationsChange(it)
                },
                colors = SwitchDefaults.colors(checkedTrackColor = button_colour),
            )
        }
        SettingRow(Icons.Rounded.Info, stringResource(R.string.settings_about), actions.onAboutClick) { Chevron() }
        SettingRow(Icons.AutoMirrored.Rounded.Logout, stringResource(R.string.settings_sign_out), actions.onSignOutClick) {}
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)?,
    trailing: @Composable () -> Unit,
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
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        trailing()
    }
}

@Composable
private fun Chevron() {
    Icon(
        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
