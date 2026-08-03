package dev.ashenarx.tools.fleet

import javax.swing.Icon

internal data class ToolWindowItem(
    val id: String,
    val title: String,
    val icon: Icon?,
    val isVisible: Boolean,
    val hasBeenOpened: Boolean,
    val isAvailable: Boolean,
    val titleHighlights: List<IntRange> = emptyList(),
)
