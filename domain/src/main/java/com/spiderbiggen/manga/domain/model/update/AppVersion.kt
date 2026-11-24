package com.spiderbiggen.manga.domain.model.update

private const val DEBUG_SUFFIX = "-debug"

/**
 * A semantic version, ordered by the semver precedence rules (pre-releases sort before releases).
 */
data class AppVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: List<String> = emptyList(),
) : Comparable<AppVersion> {

    val isPreRelease: Boolean
        get() = preRelease.isNotEmpty()

    override fun compareTo(other: AppVersion): Int {
        compareValuesBy(this, other, AppVersion::major, AppVersion::minor, AppVersion::patch).let {
            if (it != 0) return it
        }
        return when {
            preRelease.isEmpty() && other.preRelease.isEmpty() -> 0
            preRelease.isEmpty() -> 1
            other.preRelease.isEmpty() -> -1
            else -> comparePreRelease(preRelease, other.preRelease)
        }
    }

    override fun toString(): String = buildString {
        append(major).append('.').append(minor).append('.').append(patch)
        if (isPreRelease) append('-').append(preRelease.joinToString("."))
    }

    companion object {
        /**
         * Parses `1.2.3`, `v1.2.3` and `1.2.3-rc.1`. A trailing `-debug` build suffix is ignored.
         * Returns null when [raw] is not a semantic version.
         */
        fun parse(raw: String): AppVersion? {
            val text = raw.trim().removePrefix("v").removeSuffix(DEBUG_SUFFIX)
            // The first '-' starts the pre-release; identifiers may contain further hyphens.
            val numbers = text.substringBefore('-').split('.').map(::parseNumber)
            val preRelease = if ('-' in text) text.substringAfter('-').split('.') else emptyList()

            val (major, minor, patch) =
                numbers.filterNotNull().takeIf { numbers.size == 3 && it.size == 3 } ?: return null
            return AppVersion(major, minor, patch, preRelease).takeIf {
                preRelease.all(::isIdentifier)
            }
        }
    }
}

private fun comparePreRelease(left: List<String>, right: List<String>): Int {
    for ((a, b) in left.zip(right)) {
        val result = compareIdentifier(a, b)
        if (result != 0) return result
    }
    return left.size.compareTo(right.size)
}

private fun compareIdentifier(a: String, b: String): Int {
    val numericA = a.toLongOrNull()
    val numericB = b.toLongOrNull()
    return when {
        numericA != null && numericB != null -> numericA.compareTo(numericB)
        numericA != null -> -1
        numericB != null -> 1
        else -> a.compareTo(b)
    }
}

private fun parseNumber(text: String): Int? =
    text.takeIf { it.isNotEmpty() && it.all { char -> char in '0'..'9' } }?.toIntOrNull()

private fun isIdentifier(text: String): Boolean =
    text.isNotEmpty() &&
        text.all { char -> char in '0'..'9' || char in 'a'..'z' || char in 'A'..'Z' || char == '-' }
