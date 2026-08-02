package dev.ashenarx.tools.fleet.ui

import java.awt.Dimension
import kotlin.math.roundToInt

private const val WIDTH_RATIO = 0.45
private const val HEIGHT_RATIO = 0.72
private const val MIN_WIDTH = 520
private const val MIN_HEIGHT = 480
private const val MAX_WIDTH = 760
private const val MAX_HEIGHT = 760
private const val EDGE_MARGIN = 32

internal val DEFAULT_POPUP_SIZE: Dimension
    get() = Dimension(640, 640)

internal fun popupSizeFor(windowSize: Dimension): Dimension = Dimension(
    boundedSize(windowSize.width, WIDTH_RATIO, MIN_WIDTH, MAX_WIDTH),
    boundedSize(windowSize.height, HEIGHT_RATIO, MIN_HEIGHT, MAX_HEIGHT),
)

private fun boundedSize(available: Int, ratio: Double, minimum: Int, maximum: Int): Int {
    val insetAvailable = (available - EDGE_MARGIN * 2).coerceAtLeast(1)
    return (available * ratio).roundToInt()
        .coerceIn(minimum, maximum)
        .coerceAtMost(insetAvailable)
}
