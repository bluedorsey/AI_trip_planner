package com.souldream.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.ui.animation.floating
import com.souldream.ui.animation.pressScale
import com.souldream.ui.animation.staggeredEntrance
import com.souldream.ui.components.HeroBackdrop
import com.souldream.ui.theme.SouldreamTheme
import com.souldream.ui.theme.background_colour
import com.souldream.ui.theme.button_colour

private val GoogleBlue = Color(0xFF4285F4)

data class LoginUiState(
    /** True while the Google flow is in progress: disables the button and shows a spinner. */
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class LoginActions(val onGoogleSignInClick: () -> Unit = {})

/** Sign-in screen. Google is the only method, so it is a single button over the hero image. */
@Composable
fun LoginScreen(
    state: LoginUiState,
    actions: LoginActions,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().background(background_colour)) {
        HeroBackdrop(
            imageModel = R.drawable.back_image,
            height = 520.dp,
            scrollOffsetProvider = { 0f },
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.staggeredEntrance(index = 0),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Flight,
                    contentDescription = null,
                    tint = button_colour,
                    modifier = Modifier.size(32.dp).floating(amplitude = 3.dp).rotate(45f),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Text(
                text = stringResource(R.string.login_headline),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.staggeredEntrance(index = 1),
            )
            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.staggeredEntrance(index = 2),
            )
            Spacer(Modifier.height(8.dp))
            GoogleButton(
                loading = state.isLoading,
                onClick = actions.onGoogleSignInClick,
                modifier = Modifier.staggeredEntrance(index = 3),
            )
            if (state.error != null) {
                Text(
                    text = state.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun GoogleButton(loading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(32.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .pressScale(onClick, enabled = !loading, pressedScale = 0.97f)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(Color.White),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(24.dp), color = GoogleBlue, strokeWidth = 2.5.dp)
        } else {
            // ponytail: plain "G" glyph, swap for the official Google logo asset before release.
            Text("G", color = GoogleBlue, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.login_google),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F1F1F),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    SouldreamTheme(darkTheme = false, dynamicColor = false) {
        LoginScreen(state = LoginUiState(), actions = LoginActions())
    }
}
