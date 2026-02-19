package org.example.project.ime

sealed class KeyAction {
    data class Character(val char: Char) : KeyAction()
    data object Delete : KeyAction()
    data object Enter : KeyAction()
    data object Space : KeyAction()
    data object Shift : KeyAction()
    data object SymbolToggle : KeyAction()
    data object SymbolShiftToggle : KeyAction()
}

data class KeyData(
    val label: String,
    val action: KeyAction,
    val weight: Float = 1f,
    val isSpecial: Boolean = false
)

enum class KeyboardLayer { ALPHA, SYMBOLS, SYMBOLS_SHIFTED }

val qwertyAlphaRows: List<List<KeyData>> = listOf(
    "QWERTYUIOP".map { KeyData(it.toString(), KeyAction.Character(it)) },
    "ASDFGHJKL".map { KeyData(it.toString(), KeyAction.Character(it)) },
    listOf(
        KeyData("SHIFT", KeyAction.Shift, weight = 1.5f, isSpecial = true),
    ) + "ZXCVBNM".map { KeyData(it.toString(), KeyAction.Character(it)) } + listOf(
        KeyData("DEL", KeyAction.Delete, weight = 1.5f, isSpecial = true),
    ),
    listOf(
        KeyData("?123", KeyAction.SymbolToggle, weight = 1.5f, isSpecial = true),
        KeyData(",", KeyAction.Character(',')),
        KeyData("SPACE", KeyAction.Space, weight = 5f),
        KeyData(".", KeyAction.Character('.')),
        KeyData("ENTER", KeyAction.Enter, weight = 1.5f, isSpecial = true),
    )
)

val symbolRows: List<List<KeyData>> = listOf(
    "1234567890".map { KeyData(it.toString(), KeyAction.Character(it)) },
    listOf('@', '#', '$', '%', '&', '-', '+', '(', ')').map {
        KeyData(it.toString(), KeyAction.Character(it))
    },
    listOf(
        KeyData("=\\<", KeyAction.SymbolShiftToggle, weight = 1.5f, isSpecial = true),
    ) + listOf('*', '"', '\'', ':', ';', '!', '?').map {
        KeyData(it.toString(), KeyAction.Character(it))
    } + listOf(
        KeyData("DEL", KeyAction.Delete, weight = 1.5f, isSpecial = true),
    ),
    listOf(
        KeyData("ABC", KeyAction.SymbolToggle, weight = 1.5f, isSpecial = true),
        KeyData(",", KeyAction.Character(',')),
        KeyData("SPACE", KeyAction.Space, weight = 5f),
        KeyData(".", KeyAction.Character('.')),
        KeyData("ENTER", KeyAction.Enter, weight = 1.5f, isSpecial = true),
    )
)

val symbolShiftedRows: List<List<KeyData>> = listOf(
    listOf('~', '`', '|', '^', '{', '}', '[', ']', '\\', '/').map {
        KeyData(it.toString(), KeyAction.Character(it))
    },
    listOf('_', '=', '<', '>', '€', '£', '¥', '₩', '•', '°').map {
        KeyData(it.toString(), KeyAction.Character(it))
    },
    listOf(
        KeyData("?123", KeyAction.SymbolShiftToggle, weight = 1.5f, isSpecial = true),
    ) + listOf('…', '—', '«', '»', '¿', '¡', '@').map {
        KeyData(it.toString(), KeyAction.Character(it))
    } + listOf(
        KeyData("DEL", KeyAction.Delete, weight = 1.5f, isSpecial = true),
    ),
    listOf(
        KeyData("ABC", KeyAction.SymbolToggle, weight = 1.5f, isSpecial = true),
        KeyData(",", KeyAction.Character(',')),
        KeyData("SPACE", KeyAction.Space, weight = 5f),
        KeyData(".", KeyAction.Character('.')),
        KeyData("ENTER", KeyAction.Enter, weight = 1.5f, isSpecial = true),
    )
)