package com.souldream.splash

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

/** Brand tokens. */
object SoulColors {
    val Ivory = Color(0xFFFAF8F3)
    val Navy = Color(0xFF202033)
    val Lavender = Color(0xFFA69BCF)
    val LavenderDeep = Color(0xFF8A80C2)
    val LavenderSoft = Color(0xFFC6BFE2)
}

/** Timeline (ms). Mirrors the design prototype 1:1. */
object SplashTimeline {
    const val AtmosphereEnd = 800
    const val FlightStart = 800
    const val FlightEnd = 2800          // plane: tail of road -> logo position
    const val MoonStart = 1650
    const val StarStart = 2400
    const val BrandStart = 2800
    const val SweepStart = 3000
    const val SweepEnd = 3850
    const val HoldEnd = 4300
    const val Total = 5100              // end of the hand-off to the app
    const val ReducedTotal = 2500
}

object SplashEasing {
    val Out: Easing = CubicBezierEasing(.16f, 1f, .3f, 1f)
    val InOut: Easing = CubicBezierEasing(.65f, 0f, .35f, 1f)
    val Flight: Easing = CubicBezierEasing(.4f, 0f, .2f, 1f)
    val Text: Easing = CubicBezierEasing(.2f, .7f, .2f, 1f)
}

/** Normalised progress of [t] inside [a, b], optionally eased. */
fun window(t: Float, a: Float, b: Float, easing: Easing? = null): Float {
    val u = ((t - a) / (b - a)).coerceIn(0f, 1f)
    return easing?.transform(u) ?: u
}

/** Closed-form under-damped spring step response; [s] in seconds. Overshoot ~1-2% for z=.8. */
fun springStep(s: Float, zeta: Float = .8f, omega: Float = 9f): Float {
    if (s <= 0f) return 0f
    val wd = omega * sqrt(1 - zeta * zeta)
    return 1f - exp(-zeta * omega * s) *
        (cos(wd * s) + zeta / sqrt(1 - zeta * zeta) * sin(wd * s))
}

fun Float.toDegrees() = (this * 180f / PI).toFloat()
