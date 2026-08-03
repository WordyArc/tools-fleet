<p align="center">
  <img src="docs/images/logo.svg" width="112" height="112" alt="Tool Finder logo placeholder">
</p>

<h1 align="center">Tool Finder</h1>

<p align="center">
  Find and open IDE tool windows from a keyboard-driven popup.
</p>

---

Tool Finder is an IntelliJ Platform plugin for finding tool windows without navigating the IDE menus.

Press <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>T</kbd> (<kbd>Cmd</kbd>+<kbd>Shift</kbd>+<kbd>T</kbd> on macOS),
then type to filter tool windows by name. The list separates visible, previously opened and unopened tool windows.

## Screenshots

![Tool Finder popup placeholder](docs/images/popup.svg)

![Tool Finder search placeholder](docs/images/search.svg)

## Shortcuts

| Key                                      | Action                                                       |
|------------------------------------------|--------------------------------------------------------------|
| <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>T</kbd> | Open or close Tool Finder                               |
| Type                                     | Filter tool windows by name or ID                            |
| <kbd>Up</kbd> / <kbd>Down</kbd>          | Move the selection                                           |
| <kbd>Enter</kbd>                         | Activate the selected tool window                            |
| <kbd>Delete</kbd> / <kbd>Backspace</kbd> | Hide the selected active tool window when search is empty    |
| <kbd>Esc</kbd>                           | Clear the search, then close the popup                       |

On macOS, the default shortcut is <kbd>Cmd</kbd>+<kbd>Shift</kbd>+<kbd>T</kbd>.

## Configuration

Open **Settings | Tools | Tool Finder** to include unavailable tool windows in the list. They are shown dimmed and
cannot be activated in the current context.

The shortcut can be changed under **Settings | Keymap | Tool Windows**. The action is also available from
**Tools | Tool Windows**.

## Requirements

- IntelliJ Platform IDE version 2026.2 or later.
