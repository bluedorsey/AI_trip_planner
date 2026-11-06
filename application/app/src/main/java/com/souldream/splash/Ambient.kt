package com.souldream.splash

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sin

/** Honour system "Remove animations" / animator scale 0: the splash becomes a short cross-fade. */
@Composable
fun rememberReducedMotion(): Boolean {
    val ctx = LocalContext.current
    return remember {
        Settings.Global.getFloat(ctx.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/** 30 soft lavender motes on three depth layers. Deterministic seed => identical every launch. */
class ParticleField(count: Int = 30) {
    private class P(val x: Float, val y: Float, val v: Float, val d: Float, val r: Float, val ph: Float, val a: Float, val delay: Float)

    private val ps: List<P>

    init {
        var s = 7L
        fun rnd(): Float { s = (s * 1664525L + 1013904223L) and 0xFFFFFFFFL; return s / 4294967296f }
        ps = List(count) { i ->
            val d = floatArrayOf(.45f, .7f, 1f)[i % 3]
            P(150f + rnd() * 960f, -400f + rnd() * 2250f, (.006f + rnd() * .01f) * d, d,
                7f + 12f * d + rnd() * 5f, rnd() * 6.28f, .5f + rnd() * .4f, rnd() * 600f)
        }
    }

    /** [ambientMs] keeps drifting forever; [t] gates the entrance. Radial gradients, no blur. */
    fun draw(scope: DrawScope, t: Float, ambientMs: Float, handoff: Float) = with(scope) {
        val h = 2250f
        for (p in ps) {
            val y = (((p.y - p.v * ambientMs - 1850f) % h + h) % h) - 400f
            val x = p.x + sin(ambientMs / 2600f + p.ph) * 26f * p.d
            val a = window(t, p.delay, p.delay + 700f, SplashEasing.Out) * p.a *
                (.7f + .3f * sin(ambientMs / 900f + p.ph)) * (1f - .45f * handoff)
            if (a <= 0.01f) continue
            drawCircle(
                Brush.radialGradient(
                    listOf(SoulColors.Ivory.copy(alpha = .95f * a), SoulColors.Lavender.copy(alpha = .85f * a), SoulColors.Lavender.copy(alpha = 0f)),
                    Offset(x, y), p.r,
                ),
                p.r, Offset(x, y),
            )
        }
    }
}
