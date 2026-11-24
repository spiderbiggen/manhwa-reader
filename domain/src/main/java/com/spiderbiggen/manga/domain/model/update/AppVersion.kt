package com.spiderbiggen.manga.domain.model.update

private val VERSION_REGEX =
    Regex(
        """^v?(\d+)\.(\d+)\.(\d+)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+[0-9A-Za-z.-]+)?$"""
    )

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
        fun parse(raw: String): AppVersion? =
            VERSION_REGEX.matchEntire(raw.trim().removeSuffix(DEBUG_SUFFIX))?.let { match ->
                val groups = match.groupValues
                val major = groups[1].toIntOrNull()
                val minor = groups[2].toIntOrNull()
                val patch = groups[3].toIntOrNull()
                if (major == null || minor == null || patch == null) {
                    null
                } else {
                    AppVersion(major, minor, patch, groups[4].split('.').filter(String::isNotEmpty))
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
