package com.souldream

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.souldream.auth.signInWithGoogle
import com.souldream.ui.screens.login.LoginActions
import com.souldream.ui.screens.login.LoginUiState
import com.souldream.ui.screens.settings.SettingsActions
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import com.souldream.navigation.AppNavHost
import com.souldream.ui.theme.SouldreamTheme

class MainActivity : ComponentActivity() {
    private var signedIn by mutableStateOf(false)
    private var loginState by mutableStateOf(LoginUiState())
    private val authListener = FirebaseAuth.AuthStateListener { signedIn = it.currentUser != null }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The design is light-only with imagery behind the status bar, so keep the
        // system bars transparent with dark icons regardless of the device theme.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        requestHighestRefreshRate()
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
        setContent {
            // No data layer yet: AppNavHost's default UI states are all "loading",
            // so both screens render their shimmer placeholders.
            SouldreamTheme(darkTheme = false, dynamicColor = false) {
                AppNavHost(
                    signedIn = signedIn,
                    loginState = loginState,
                    loginActions = LoginActions(onGoogleSignInClick = ::signIn),
                    settingsActions = SettingsActions(onSignOutClick = ::signOut),
                )
            }
        }
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        super.onDestroy()
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut() // authListener flips signedIn, showing Login
        // Also forget the Google credential so the next sign-in starts clean.
        lifecycleScope.launch { CredentialManager.create(this@MainActivity).clearCredentialState(ClearCredentialStateRequest()) }
    }

    private fun signIn() {
        if (loginState.isLoading) return
        loginState = LoginUiState(isLoading = true)
        lifecycleScope.launch {
            loginState = try {
                signInWithGoogle(this@MainActivity, getString(R.string.default_web_client_id))
                LoginUiState() // authListener flips signedIn, which swaps Login for the app
            } catch (e: CancellationException) {
                throw e
            } catch (_: GetCredentialCancellationException) {
                LoginUiState() // user dismissed the account sheet
            } catch (_: Exception) {
                LoginUiState(error = getString(R.string.login_error))
            }
        }
    }

    /**
     * Many OEMs keep third-party apps at 60 Hz unless they ask for more. Pick the fastest
     * display mode at the current resolution (120/144/165 Hz...), so the switch is seamless.
     */
    @Suppress("DEPRECATION")
    private fun requestHighestRefreshRate() {
        val display = windowManager.defaultDisplay
        val current = display.mode
        val best = display.supportedModes
            .filter { it.physicalWidth == current.physicalWidth && it.physicalHeight == current.physicalHeight }
            .maxByOrNull { it.refreshRate } ?: return
        window.attributes = window.attributes.apply { preferredDisplayModeId = best.modeId }
    }
}
