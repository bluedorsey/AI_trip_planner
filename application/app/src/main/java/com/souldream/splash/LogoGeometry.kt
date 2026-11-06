package com.souldream.splash

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.vector.PathParser
import kotlin.math.atan2

/**
 * Logo artwork in the design's coordinate space (a 1000-wide window starting at x=130).
 * Pure data + geometry, parsed once.
 */
object LogoPaths {
    const val ROAD =
        "M726 430 C690 452 628 470 596 498 C584 510 588 524 606 531 C655 550 705 548 742 566 " +
        "C780 586 778 630 738 652 C700 673 590 676 452 703 C545 676 640 660 676 636 " +
        "C712 612 698 596 660 589 C610 580 548 574 530 548 C515 524 545 500 600 482 " +
        "C650 466 700 448 726 430 Z"

    /** Flight approach (off-screen bottom-left to the road's tail). */
    const val APPROACH = "M-60 1230 C160 1200 160 790 452 702"

    /** Centre line of the S road: used for the draw-on and for the plane to follow. */
    const val CENTERLINE =
        "M452 702 C540 680 640 672 712 640 C775 612 770 575 710 558 " +
        "C650 542 585 545 556 525 C540 512 560 492 610 475 C660 458 700 445 726 432"

    /** Last hop: road tip to the plane's resting spot beside the emblem. */
    const val EXIT = "M726 432 C746 424 772 408 800 394"

    val RAIL = APPROACH + " " + CENTERLINE.removePrefix("M452 702") + " " + EXIT.removePrefix("M726 432")

    const val PLANE =
        "M52 0 C52 -4 46 -6 40 -6 L12 -6 L-6 -42 L-16 -42 L-10 -6 L-34 -6 L-42 -18 L-49 -18 " +
        "L-45 0 L-49 18 L-42 18 L-34 6 L-10 6 L-16 42 L-6 42 L12 6 L40 6 C46 6 52 4 52 0 Z"

    const val STAR =
        "M0 -34 C3 -8 8 -3 34 0 C8 3 3 8 0 34 C-3 8 -8 3 -34 0 C-8 -3 -3 -8 0 -34 Z"

    val MoonCenter = Offset(630f, 528f)
    const val MoonRadius = 150f
    val StarPos = Offset(431f, 461f)
    val Pivot = Offset(630f, 620f)
    const val RoadStroke = 76f
    const val PlaneScale = 1.3f
}

/** Holds the flight rail (approach + centre line + exit) as one measurable contour. */
class LogoGeometry {
    val road = PathParser().parsePathString(LogoPaths.ROAD).toPath()
    val star = PathParser().parsePathString(LogoPaths.STAR).toPath()
    val plane = PathParser().parsePathString(LogoPaths.PLANE).toPath()
    val centerline = PathParser().parsePathString(LogoPaths.CENTERLINE).toPath()

    /** approach + centre line + exit as ONE contour: the plane's rail and the trail share it. */
    private val rail = PathMeasure().also {
        it.setPath(PathParser().parsePathString(LogoPaths.RAIL).toPath(), false)
    }
    private val centerMeasure = PathMeasure().also { it.setPath(centerline, false) }

    val lenA = len(LogoPaths.APPROACH)
    val lenC = len(LogoPaths.CENTERLINE)
    val lenE = len(LogoPaths.EXIT)
    val total = rail.length

    private fun len(d: String) =
        PathMeasure().also { it.setPath(PathParser().parsePathString(d).toPath(), false) }.length

    fun pointAt(distance: Float): Offset = rail.getPosition(distance.coerceIn(0f, total))

    /** Trail: rail from 0 to [distance] (reuses [out]). */
    fun railSegment(distance: Float, out: Path): Path {
        out.reset()
        rail.getSegment(0f, distance.coerceIn(0f, total), out, true)
        return out
    }

    /** Heading in degrees using a +-30 chord: smooths the two S-turns so the plane banks gently. */
    fun headingAt(distance: Float, previous: Float?): Float {
        val a = pointAt(distance - 30f)
        val b = pointAt(distance + 30f)
        var ang = atan2(b.y - a.y, b.x - a.x).toDegrees()
        if (previous != null) {
            while (ang - previous > 180f) ang -= 360f
            while (ang - previous < -180f) ang += 360f
        }
        return ang
    }

    /** Sub-path of the centre line from 0..[fraction], for the progressive road draw-on. */
    fun centerlineSegment(fraction: Float, out: Path): Path {
        out.reset()
        centerMeasure.getSegment(0f, lenC * fraction.coerceIn(0f, 1f), out, true)
        return out
    }
}
