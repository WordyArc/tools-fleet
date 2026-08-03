package dev.ashenarx.tools.fleet

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.testFramework.junit5.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

@TestApplication
class PluginRuntimeTest {

    @Test
    fun `show tool windows action is registered from plugin xml`() {
        val action = ActionManager.getInstance().getAction("ToolsFleet.ShowToolWindows")

        assertNotNull(action)
        assertEquals(ShowToolWindowsAction::class.java, action.javaClass)
    }
}
