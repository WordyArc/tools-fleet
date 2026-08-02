@file:Suppress("UnstableApiUsage")

package dev.ashenarx.tools.fleet

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.WindowManager
import dev.ashenarx.tools.fleet.ui.DEFAULT_POPUP_SIZE
import dev.ashenarx.tools.fleet.ui.ToolWindowsPopup
import dev.ashenarx.tools.fleet.ui.popupSizeFor
import dev.ashenarx.tools.fleet.settings.ToolFinderSettings
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import javax.swing.JComponent

class ShowToolWindowsAction : DumbAwareAction() {
    private var activePopup: JBPopup? = null

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        activePopup?.takeIf(JBPopup::isVisible)?.let {
            it.cancel()
            return
        }

        val manager = ToolWindowManager.getInstance(project)
        val showUnavailable = service<ToolFinderSettings>().showUnavailableToolWindows
        val items = manager.toolWindowIds
            .mapNotNull(manager::getToolWindow)
            .filter { it.isAvailable || showUnavailable }
            .map { toolWindow ->
                ToolWindowItem(
                    id = toolWindow.id,
                    title = toolWindow.stripeTitle.ifBlank { toolWindow.id },
                    icon = toolWindow.icon,
                    isOpen = toolWindow.isAvailable && toolWindow.contentManagerIfCreated != null,
                    isAvailable = toolWindow.isAvailable,
                )
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, ToolWindowItem::title))

        val windowSize = WindowManager.getInstance().getFrame(project)?.size
        val popupSize = windowSize?.let(::popupSizeFor) ?: DEFAULT_POPUP_SIZE
        var selectedId: String? = null
        var popup: JBPopup? = null

        val panel = JewelComposePanel {
            SwingBridgeTheme {
                ToolWindowsPopup(
                    items = items,
                    onClose = { popup?.cancel() },
                    onSelect = { id ->
                        selectedId = id
                        popup?.cancel()
                    },
                )
            }
        }.apply {
            preferredSize = popupSize
        }

        popup = createPopup(panel).also { created ->
            activePopup = created
            created.setFinalRunnable {
                if (activePopup === created) activePopup = null
                selectedId?.let { manager.getToolWindow(it)?.activate(null) }
            }
            created.showCenteredInCurrentWindow(project)
        }
    }

    private fun createPopup(panel: JComponent): JBPopup =
        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, panel)
            .setRequestFocus(true)
            .setFocusable(true)
            .setCancelKeyEnabled(false)
            .setCancelOnClickOutside(true)
            .setCancelOnOtherWindowOpen(true)
            .setMovable(false)
            .setResizable(false)
            .createPopup()
}
