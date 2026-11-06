package com.souldream.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import com.souldream.R
import kotlin.math.PI
import kotlin.math.sin

/** EB Garamond Medium. On API 26+ the variable font is set to weight 500; older phones get Regular. */
private val Garamond = FontFamily(Font(R.font.eb_garamond, FontWeight.Medium))

/**
 * The Soul Dream splash, drawn over the app. It fades out in the hand-off, revealing the app
 * (which is already composed underneath), then calls [onFinished] so the caller can remove it.
 *
 * Performance notes (from the design)
 *  - ONE Animatable<Float> clock (ms), read only in draw/graphicsLayer lambdas, so the tree
 *    never recomposes while animating.
 *  - Frame-time driven: identical timing at 60/90/120 Hz.
 *  - Paths are parsed once; no blur / RenderEffect; glows are radial gradients.
 */
@Composable
fun SoulDreamSplash(
    modifier: Modifier = Modifier,
    reducedMotion: Boolean = rememberReducedMotion(),
    onFinished: () -> Unit = {},
) {
    val clock = remember { Animatable(0f) }
    val total = if (reducedMotion) SplashTimeline.ReducedTotal else SplashTimeline.Total

    LaunchedEffect(reducedMotion) {
        clock.snapTo(0f)
        clock.animateTo(total.toFloat(), tween(total, easing = LinearEasing))
        onFinished()
    }

    Box(
        modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 1f - handoff(clock.value, reducedMotion) }
            .background(SoulColors.Ivory)
            .pointerInput(Unit) {}, // the app underneath must not be tappable until the splash is gone
    ) {
        SplashCanvas(clock = { clock.value }, reducedMotion = reducedMotion)
    }
}

@Composable
private fun SplashCanvas(clock: () -> Float, reducedMotion: Boolean) {
    val geo = remember { LogoGeometry() }
    val particles = remember { ParticleField() }
    val measurer = rememberTextMeasurer()
    val scratch = remember { Path() }
    val trail = remember { Path() }
    val heading = remember { floatArrayOf(Float.NaN) }

    // InfiniteTransition ONLY for ambient motion (breathing + particle drift).
    val ambient = rememberInfiniteTransition(label = "ambient")
    val breath by ambient.animateFloat(
        0f, 1f, infiniteRepeatable(tween(5200, easing = LinearEasing), RepeatMode.Reverse), label = "breath",
    )
    val drift by ambient.animateFloat(
        0f, 1f, infiniteRepeatable(tween(60_000, easing = LinearEasing)), label = "drift",
    )

    Canvas(Modifier.fillMaxSize()) {
        val raw = clock()
        val t = if (reducedMotion) 3900f else raw
        val fade = if (reducedMotion) window(raw, 0f, 700f) else 1f
        val tr = handoff(raw, reducedMotion)

        // Fit the 1000-wide design window into the canvas, logo centred at 45% height.
        val s = size.width / 1000f
        val originX = 130f
        val originY = 620f - (size.height * .45f) / s

        withTransform({
            scale(s, s, Offset.Zero)
            translate(-originX, -originY)
        }) {
            drawAtmosphere(t, fade, tr, breath)
            if (!reducedMotion) particles.draw(this, t, drift * 60_000f, tr)
            drawLogo(geo, measurer, scratch, trail, heading, t, fade, tr)
        }
    }
}

private fun handoff(raw: Float, reduced: Boolean) =
    if (reduced) window(raw, 1700f, 2500f, SplashEasing.InOut)
    else window(raw, SplashTimeline.HoldEnd.toFloat(), SplashTimeline.Total.toFloat(), SplashEasing.InOut)

private fun DrawScope.drawAtmosphere(t: Float, fade: Float, tr: Float, breath: Float) {
    val atm = window(t, 0f, 800f, SplashEasing.Out)
    fun blob(c: Offset, r: Float, color: Color, alpha: Float) {
        val sc = (.8f + .2f * atm) * (1f + .035f * breath)
        drawCircle(
            Brush.radialGradient(listOf(color.copy(alpha = alpha), Color.Transparent), c, r * sc),
            r * sc, c,
        )
    }
    blob(Offset(300f, 80f), 720f, SoulColors.Lavender, .55f * .85f * atm * fade * (1 - .3f * tr))
    blob(Offset(1010f, 1380f), 680f, SoulColors.Lavender, .55f * .7f * atm * fade * (1 - .3f * tr))
    val moonLift = window(t, 1700f, 2700f)
    blob(Offset(640f, 560f), 470f, SoulColors.LavenderSoft, .7f * (atm * .3f + .7f * moonLift) * fade)
}

private fun DrawScope.drawLogo(
    geo: LogoGeometry, measurer: TextMeasurer, scratch: Path, trail: Path, heading: FloatArray,
    t: Float, fade: Float, tr: Float,
) {
    val settle = 1f + .035f * (1f - springStep((t - 3000f) / 1000f, .8f, 6f))
    val scale = settle * .8f * (1f - .375f * tr) // .8 base keeps side margins
    val dy = -590f * tr
    val pivot = LogoPaths.Pivot

    withTransform({
        translate(pivot.x, pivot.y + dy)
        scale(scale, scale, Offset.Zero)
        translate(-pivot.x, -pivot.y)
    }) {
        // ---- Scene 2/3: flight --------------------------------------------------------
        val d = geo.total * window(t, 800f, 2800f, SplashEasing.Flight)

        // Lavender trail: length == distance flown; hands over to the navy road, then fades out.
        val trailAlpha = window(t, 800f, 1100f) * (1f - window(t, 2000f, 3000f)) * .9f * fade
        drawPath(geo.railSegment(d, trail), SoulColors.Lavender.copy(alpha = trailAlpha),
            style = Stroke(4f, cap = StrokeCap.Round))

        // ---- Scene 3: moon scales in on a spring --------------------------------------
        val ms = .55f + .45f * springStep((t - 1650f) / 1000f, .85f, 7f)
        val moonA = window(t, 1650f, 2250f, SplashEasing.Out) * fade
        val lift = (1f - window(t, 1650f, 2650f, SplashEasing.Out)) * 30f
        withTransform({ translate(0f, lift); scale(ms, ms, LogoPaths.MoonCenter) }) {
            drawCircle(
                Brush.verticalGradient(
                    0f to SoulColors.LavenderDeep.copy(alpha = moonA),
                    .5f to SoulColors.LavenderSoft.copy(alpha = .78f * moonA),
                    1f to SoulColors.Ivory.copy(alpha = 0f),
                    startY = LogoPaths.MoonCenter.y - LogoPaths.MoonRadius,
                    endY = LogoPaths.MoonCenter.y + LogoPaths.MoonRadius,
                ),
                LogoPaths.MoonRadius, LogoPaths.MoonCenter,
            )
        }

        // ---- Road: revealed progressively by clipping to the stroked centre-line ------
        val pr = ((d - geo.lenA - 14f) / geo.lenC).coerceIn(0f, 1f)
        if (pr > 0f) {
            val revealed = geo.centerlineSegment(pr, scratch)
            // Stroke-as-clip: draw the road only where the thick centre-line segment has passed.
            clipPath(strokeOutline(revealed, LogoPaths.RoadStroke)) {
                drawPath(geo.road, SoulColors.Navy.copy(alpha = fade))
            }
        }

        // ---- Star sparkle ------------------------------------------------------------
        val starS = springStep((t - 2400f) / 1000f, .55f, 11f) *
            (1f + .09f * sin(((t - 3000f).coerceAtLeast(0f)) / 420f) * window(t, 3000f, 3300f))
        withTransform({ translate(LogoPaths.StarPos.x, LogoPaths.StarPos.y); scale(starS, starS, Offset.Zero) }) {
            drawPath(geo.star, SoulColors.Lavender.copy(alpha = window(t, 2400f, 2650f) * fade))
        }

        // ---- Scene 4: brand name, two words, staggered 110ms --------------------------
        drawWord(measurer, "Soul", -1, 0f, t, fade)
        drawWord(measurer, "Dream", +1, 110f, t, fade)

        // ---- Plane --------------------------------------------------------------------
        val p = geo.pointAt(d)
        val prev = heading[0].takeIf { !it.isNaN() }
        val ang = geo.headingAt(d, prev).also { heading[0] = it }
        val depth = .72f + .28f * window(d / geo.total, 0f, .75f, SplashEasing.InOut)
        val bump = 1f + .05f * (1f - springStep((t - 2700f) / 1000f, .8f, 8f)) * window(t, 2400f, 2700f)
        val ps = LogoPaths.PlaneScale * depth * bump
        withTransform({ translate(p.x, p.y); rotate(ang, Offset.Zero); scale(ps, ps, Offset.Zero) }) {
            drawPath(geo.plane, SoulColors.Navy.copy(alpha = window(t, 800f, 1100f) * fade))
        }

        // ---- Light sweep across the finished logo --------------------------------------
        val sw = window(t, 3000f, 3850f, SplashEasing.Flight)
        val x = 120f + 1000f * sw
        drawPath(
            geo.road,
            Brush.horizontalGradient(
                listOf(Color.Transparent, Color(0xFFD6D0F0).copy(alpha = .85f), Color.Transparent),
                startX = x - 130f, endX = x + 130f,
            ),
            alpha = sin(PI.toFloat() * sw).coerceAtLeast(0f),
        )
    }
}

/** Outline of [src] stroked at [width]: used as a clip so the road "draws itself" along its centre line. */
private fun strokeOutline(src: Path, width: Float): Path {
    val out = android.graphics.Path()
    android.graphics.Paint().apply {
        style = android.graphics.Paint.Style.STROKE; strokeWidth = width
    }.getFillPath(src.asAndroidPath(), out)
    return out.asComposePath()
}

private fun DrawScope.drawWord(
    measurer: TextMeasurer, word: String, side: Int, delay: Float, t: Float, fade: Float,
) {
    val style = TextStyle(
        color = SoulColors.Navy.copy(alpha = window(t, 2800f + delay, 3350f + delay, SplashEasing.Out) * fade),
        fontFamily = Garamond,
        fontWeight = FontWeight.Medium,
        // The canvas is already scaled to design units, so the size is 190 *design units*, not 190sp
        // (190sp would be ~3x too big on a 3x-density phone).
        fontSize = 190f.toSp(),
    )
    val layout = measurer.measure(word, style)
    val gap = 52f
    val soulW = measurer.measure("Soul", style).size.width
    val dreamW = measurer.measure("Dream", style).size.width
    val x0 = 630f - (soulW + gap + dreamW) / 2f
    val x = if (side < 0) x0 else x0 + soulW + gap
    val rise = 30f * (1f - window(t, 2800f + delay, 3600f + delay, SplashEasing.Text))
    drawText(layout, topLeft = Offset(x, 885f - layout.firstBaseline + rise))
}
