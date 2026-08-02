package dev.ashenarx.tools.fleet

import javax.swing.Icon

internal data class ToolWindowItem(
    val id: String,
    val title: String,
    val icon: Icon?,
    val isOpen: Boolean,
)
