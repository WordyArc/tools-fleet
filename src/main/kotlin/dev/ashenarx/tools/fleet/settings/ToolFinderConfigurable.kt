package dev.ashenarx.tools.fleet.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import dev.ashenarx.tools.fleet.ToolsFleetBundle
import dev.ashenarx.tools.fleet.ToolsFleetBundle.message

internal class ToolFinderConfigurable : BoundConfigurable(message("settings.title")) {
    override fun createPanel(): DialogPanel {
        val settings = service<ToolFinderSettings>()

        return panel {
            row {
                checkBox(ToolsFleetBundle.message("settings.show.unavailable"))
                    .bindSelected(settings::showUnavailableToolWindows)
                    .comment(message("settings.show.unavailable.comment"))
            }
            row {
                checkBox(ToolsFleetBundle.message("settings.show.shortcuts"))
                    .bindSelected(settings::showToolWindowShortcuts)
                    .comment(message("settings.show.shortcuts.comment"))
            }
        }
    }
}
