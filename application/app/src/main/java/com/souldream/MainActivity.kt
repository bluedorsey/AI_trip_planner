package com.souldream

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.souldream.navigation.AppNavHost
import com.souldream.ui.theme.SouldreamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The design is light-only with imagery behind the status bar, so keep the
        // system bars transparent with dark icons regardless of the device theme.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            // No data layer yet: AppNavHost's default UI states are all "loading",
            // so both screens render their shimmer placeholders.
            SouldreamTheme(darkTheme = false, dynamicColor = false) {
                AppNavHost()
            }
        }
    }
}
