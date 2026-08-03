<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="docs/images/logo_dark.svg">
    <img src="docs/images/logo.svg" width="112" height="112" alt="Tool Finder logo">
  </picture>
</p>

<h1 align="center">Tool Finder</h1>

<p align="center">
  Find and open IDE tool windows from a keyboard-driven popup.
</p>

---

Tool Finder is an IntelliJ Platform plugin for finding tool windows without navigating the IDE menus.

Press <kbd>Ctrl</kbd>+<kbd>&#92;</kbd> (<kbd>Cmd</kbd>+<kbd>&#92;</kbd> on macOS), then type to filter tool windows by
name. The list separates visible, previously opened and unopened tool windows, and each row shows the tool window's own
IDE shortcut when it has one.

## Screenshots

![The Tool Finder popup listing active, recent and new tool windows](docs/images/popup.png)

![Filtering the list by typing, with the matched characters highlighted](docs/images/search.png)

## Shortcuts

| Key                                      | Action                                                           |
|------------------------------------------|------------------------------------------------------------------|
| <kbd>Ctrl</kbd>+<kbd>&#92;</kbd>         | Open or close Tool Finder                                        |
| Type                                     | Filter tool windows by name or ID                                |
| <kbd>Up</kbd> / <kbd>Down</kbd>          | Move the selection                                               |
| <kbd>Enter</kbd>                         | Activate the selected tool window, or hide it when it is focused |
| <kbd>Delete</kbd> / <kbd>Backspace</kbd> | Hide the selected active tool window when search is empty        |
| <kbd>Esc</kbd>                           | Clear the search, then close the popup                           |

On macOS, the default shortcut is <kbd>Cmd</kbd>+<kbd>&#92;</kbd>.

## Configuration

Open **Settings | Tools | Tool Finder** to include unavailable tool windows in the list. They are shown dimmed and
cannot be activated in the current context.

The same page toggles the tool window shortcuts shown next to each name. They are enabled by default.

The shortcut can be changed under **Settings | Keymap** by searching for **Tool Finder**. The action is also available
from **Tools | Tool Finder**.

## Requirements

- IntelliJ Platform IDE version 2026.2 or later.
