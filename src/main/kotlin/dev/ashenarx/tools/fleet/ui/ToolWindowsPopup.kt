package dev.ashenarx.tools.fleet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.ashenarx.tools.fleet.ToolWindowItem
import dev.ashenarx.tools.fleet.ToolWindowMatcher
import dev.ashenarx.tools.fleet.ToolsFleetBundle
import org.jetbrains.jewel.bridge.retrieveColorOrNull
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.theme.simpleListItemStyle
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.max
import javax.swing.Icon as SwingIcon

@Composable
internal fun ToolWindowsPopup(
    items: List<ToolWindowItem>,
    onClose: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val search = rememberTextFieldState()
    val query = search.text.toString()
    val filtered = remember(items, query) { items.filterAndRank(query) }
    val rows = remember(filtered) { filtered.toRows() }
    val displayedItems = remember(rows) { rows.mapNotNull { (it as? PopupRow.Item)?.value } }
    var selectedId by remember { mutableStateOf(displayedItems.firstOrNull()?.id) }

    LaunchedEffect(query, displayedItems) {
        selectedId = displayedItems.firstOrNull()?.id
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JewelTheme.globalColors.panelBackground)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(ToolsFleetBundle.message("popup.title"), modifier = Modifier.padding(horizontal = 4.dp))

        TextField(
            state = search,
            placeholder = { Text(ToolsFleetBundle.message("popup.search.placeholder")) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                    when (event.key) {
                        Key.DirectionDown -> {
                            selectedId = displayedItems.moveSelection(selectedId, 1)
                            true
                        }

                        Key.DirectionUp -> {
                            selectedId = displayedItems.moveSelection(selectedId, -1)
                            true
                        }

                        Key.Enter -> {
                            selectedId?.let(onSelect)
                            true
                        }

                        Key.Escape -> {
                            if (search.text.isEmpty()) onClose() else search.setTextAndPlaceCursorAtEnd("")
                            true
                        }

                        else -> false
                    }
                },
        )

        if (rows.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = ToolsFleetBundle.message("popup.empty"),
                    color = JewelTheme.globalColors.text.disabled,
                )
            }
        } else {
            ToolWindowRows(
                rows = rows,
                selectedId = selectedId,
                onSelect = onSelect,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ToolWindowRows(
    rows: List<PopupRow>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(rows, selectedId) {
        val index = rows.indexOfFirst { it is PopupRow.Item && it.value.id == selectedId }
        val visibleItems = listState.layoutInfo.visibleItemsInfo
        if (index >= 0 && visibleItems.isNotEmpty() && visibleItems.none { it.index == index }) {
            listState.animateScrollToItem(index)
        }
    }

    LazyColumn(state = listState, modifier = modifier) {
        items(rows, key = PopupRow::key) { row ->
            when (row) {
                is PopupRow.Section -> SectionTitle(row.title)
                is PopupRow.Item -> ToolWindowRow(
                    item = row.value,
                    selected = row.value.id == selectedId,
                    onClick = { onSelect(row.value.id) },
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = JewelTheme.globalColors.text.disabled,
        modifier = Modifier.padding(start = 6.dp, top = 8.dp, bottom = 4.dp),
    )
}

@Composable
private fun ToolWindowRow(item: ToolWindowItem, selected: Boolean, onClick: () -> Unit) {
    val style = JewelTheme.simpleListItemStyle
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val selectedBackground = retrieveColorOrNull("List.selectionBackground")
        ?: JewelTheme.globalColors.outlines.focused
    val hoverBackground = retrieveColorOrNull("List.hoverBackground")
        ?: selectedBackground.copy(alpha = 0.4f)
    val background = when {
        selected -> selectedBackground
        hovered -> hoverBackground
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(JewelTheme.globalMetrics.rowHeight)
            .padding(horizontal = 2.dp)
            .background(background, RoundedCornerShape(style.metrics.selectionBackgroundCornerSize))
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ToolWindowIcon(item.icon)
        Spacer(Modifier.width(8.dp))
        Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ToolWindowIcon(icon: SwingIcon?) {
    val painter = remember(icon) { icon?.toPainterOrNull() }
    if (painter == null) {
        Spacer(Modifier.size(16.dp))
    } else {
        Image(painter = painter, contentDescription = null, modifier = Modifier.size(16.dp))
    }
}

private fun SwingIcon.toPainterOrNull(): Painter? = runCatching {
    val image = BufferedImage(max(1, iconWidth), max(1, iconHeight), BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    try {
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        paintIcon(null, graphics, 0, 0)
    } finally {
        graphics.dispose()
    }
    BitmapPainter(image.toComposeImageBitmap())
}.getOrNull()

private sealed interface PopupRow {
    val key: String

    data class Section(val title: String) : PopupRow {
        override val key: String = "section:$title"
    }

    data class Item(val value: ToolWindowItem) : PopupRow {
        override val key: String = "item:${value.id}"
    }
}

private fun List<ToolWindowItem>.toRows(): List<PopupRow> = buildList {
    val active = filter(ToolWindowItem::isOpen)
    val inactive = filterNot(ToolWindowItem::isOpen)

    if (active.isNotEmpty()) {
        add(PopupRow.Section(ToolsFleetBundle.message("popup.section.active")))
        active.mapTo(this, PopupRow::Item)
    }
    if (inactive.isNotEmpty()) {
        add(PopupRow.Section(ToolsFleetBundle.message("popup.section.new")))
        inactive.mapTo(this, PopupRow::Item)
    }
}

private fun List<ToolWindowItem>.filterAndRank(query: String): List<ToolWindowItem> {
    if (query.isBlank()) return this
    val matcher = ToolWindowMatcher(query)

    return mapNotNull { item -> matcher.degreeOrNull(item.searchText)?.let { degree -> item to degree } }
        .sortedByDescending(Pair<ToolWindowItem, Int>::second)
        .map(Pair<ToolWindowItem, Int>::first)
}

private val ToolWindowItem.searchText: String
    get() = "$title $id"

private fun List<ToolWindowItem>.moveSelection(currentId: String?, delta: Int): String? {
    if (isEmpty()) return null
    val current = indexOfFirst { it.id == currentId }
    if (current < 0) return first().id

    return this[(current + delta).coerceIn(0, lastIndex)].id
}
