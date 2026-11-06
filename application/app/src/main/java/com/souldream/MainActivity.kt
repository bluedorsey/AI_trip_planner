package com.souldream

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import com.google.firebase.FirebaseNetworkException
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.souldream.auth.signInWithGoogle
import com.souldream.ui.screens.explore.ExploreActions
import com.souldream.ui.screens.home.HomeActions
import com.souldream.ui.screens.login.LoginActions
import com.souldream.ui.screens.login.LoginUiState
import com.souldream.ui.screens.saved.SavedActions
import com.souldream.model.TripActivity
import com.souldream.splash.SoulDreamSplash
import com.souldream.ui.screens.settings.SettingsActions
import com.souldream.ui.screens.settings.SettingsUiState
import com.souldream.ui.screens.tripplan.TripPlanActions
import com.souldream.ui.state.dataOrNull
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import com.souldream.navigation.AppNavHost
import com.souldream.ui.theme.SouldreamTheme

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
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
            val homeState by vm.homeState.collectAsState()
            val savedState by vm.savedState.collectAsState()
            val exploreState by vm.exploreState.collectAsState()
            val tripPlanState by vm.tripPlanState.collectAsState()
            val user = if (signedIn) FirebaseAuth.getInstance().currentUser else null
            SouldreamTheme(darkTheme = false, dynamicColor = false) {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                Box(Modifier.fillMaxSize()) {
                    // The app is composed under the splash, which fades out to reveal it.
                    AppNavHost(
                        signedIn = signedIn,
                        loginState = loginState,
                        loginActions = LoginActions(onGoogleSignInClick = ::signIn),
                        homeState = homeState,
                        homeActions = homeActions,
                        savedState = savedState,
                        savedActions = savedActions,
                        exploreState = exploreState,
                        exploreActions = exploreActions,
                        tripPlanState = tripPlanState,
                        tripPlanActions = tripPlanActions,
                        settingsState = SettingsUiState(
                            name = user?.displayName.orEmpty(),
                            email = user?.email.orEmpty(),
                        ),
                        settingsActions = SettingsActions(onSignOutClick = ::signOut),
                    )
                    if (showSplash) SoulDreamSplash(onFinished = { showSplash = false })
                }
            }
        }
        lifecycleScope.launch {
            vm.messages.collect { Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show() }
        }
    }

    // Hoisted so AppNavHost's remember(actions) isn't invalidated on every recomposition.
    private val homeActions by lazy {
        HomeActions(
            onSearchSubmit = { query, categoryId -> vm.generate(query, categoryId) },
            onDestinationClick = { vm.generate(getString(R.string.plan_trip_to, it.name), null) },
            onPlanClick = { vm.openTrip(it.id) },
        )
    }
    private val exploreActions by lazy {
        ExploreActions(onDestinationClick = { vm.generate(getString(R.string.plan_trip_to, it.name), null) })
    }
    private val savedActions by lazy { SavedActions(onPlanClick = { vm.openTrip(it.id) }) }
    private val tripPlanActions by lazy {
        TripPlanActions(
            onBack = vm::closeTrip,
            onShare = ::shareTrip,
            onToggleSave = vm::setSaved,
            onActivityClick = ::openInMaps,
        )
    }

    /** Opens the place in Google Maps (or the browser): the title plus the city finds the exact spot. */
    private fun openInMaps(activity: TripActivity) {
        val city = vm.tripPlanState.value.plan.dataOrNull()?.location.orEmpty()
        val query = Uri.encode("${activity.title} $city".trim())
        val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, uri)) }
    }

    private fun shareTrip() {
        val plan = vm.tripPlanState.value.plan.dataOrNull() ?: return
        val text = buildString {
            appendLine(plan.title)
            appendLine(listOf(plan.duration, plan.dateRange, plan.budget).filter { it.isNotBlank() }.joinToString(" • "))
            plan.days.forEach { day ->
                appendLine()
                appendLine(getString(R.string.share_day, day.dayNumber, day.theme))
                day.activities.forEach { appendLine("• ${it.title}") }
            }
            appendLine()
            append(getString(R.string.share_footer))
        }
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(send, null))
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
            } catch (e: Exception) {
                Log.e("SignIn", "Google sign-in failed", e) // adb logcat -s SignIn
                val message = when (e) {
                    is NoCredentialException -> R.string.login_error_no_account
                    is GetCredentialProviderConfigurationException, is GetCredentialUnsupportedException ->
                        R.string.login_error_play_services
                    is FirebaseNetworkException -> R.string.login_error_network
                    else -> R.string.login_error
                }
                LoginUiState(error = getString(message))
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
