package dev.ashenarx.tools.fleet.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
@State(
    name = "ToolFinderSettings",
    storages = [Storage("toolFinder.xml")],
    category = SettingsCategory.TOOLS,
)
internal class ToolFinderSettings : SimplePersistentStateComponent<ToolFinderSettings.State>(State()) {
    var showUnavailableToolWindows: Boolean
        get() = state.showUnavailableToolWindows
        set(value) {
            state.showUnavailableToolWindows = value
        }

    internal class State : BaseState() {
        var showUnavailableToolWindows by property(false)
    }
}
