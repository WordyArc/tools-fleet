package dev.ashenarx.tools.fleet

import com.intellij.psi.codeStyle.MinusculeMatcher
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.util.text.NameUtilCore

internal class ToolWindowMatcher(query: String) {
    private val delegate: MinusculeMatcher? =
        query.toPatternOrNull()?.let { NameUtil.buildMatcher(it).typoTolerant().build() }

    fun matchOrNull(text: String): ToolWindowMatch? {
        val matcher = delegate ?: return null
        val fragments = matcher.match(text) ?: return null
        return ToolWindowMatch(
            degree = matcher.matchingDegree(text, false, fragments),
            highlights = fragments.map { it.startOffset until it.endOffset },
        )
    }
}

internal data class ToolWindowMatch(val degree: Int, val highlights: List<IntRange>)

private fun String.toPatternOrNull(): String? {
    if (isBlank()) return null

    val pattern = NameUtilCore.nameToWordList(this).joinToString("*")
    return if (pattern.startsWith("*")) pattern else "*$pattern"
}
