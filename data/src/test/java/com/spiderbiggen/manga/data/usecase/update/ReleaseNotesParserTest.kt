package com.spiderbiggen.manga.data.usecase.update

import com.spiderbiggen.manga.domain.model.update.ChangeKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesParserTest {
    private val parser = ReleaseNotesParser()

    @Test
    fun `given a release body when parsed then messages are stripped of commit scope and author`() {
        val groups = parser.parse(RELEASE_1_32_4)

        assertEquals(listOf(ChangeKind.Fix), groups.map { it.kind })
        assertEquals(
            listOf(
                "Sort manga overview by last chapter update",
                "Derive manga unread state from local chapters only",
            ),
            groups.single().messages,
        )
    }

    @Test
    fun `given refactor and chore sections when parsed then they are dropped`() {
        val groups = parser.parse(RELEASE_WITH_ALL_SECTIONS)

        assertEquals(
            listOf(ChangeKind.Feature, ChangeKind.Fix, ChangeKind.Performance),
            groups.map { it.kind },
        )
        assertEquals(listOf("Add update notice"), groups[0].messages)
        assertEquals(listOf("Fix crash on launch"), groups[1].messages)
        assertEquals(listOf("Cache cover images"), groups[2].messages)
    }

    @Test
    fun `given only chores when parsed then there are no groups`() {
        assertTrue(
            parser
                .parse(
                    "### :wrench: Chores\n- [`abc1234`](https://x/y) - **deps**: bump *(PR #1 by @a)*\n"
                )
                .isEmpty()
        )
    }

    @Test
    fun `given the older commit author format when parsed then the suffix is stripped`() {
        val groups =
            parser.parse(
                "### :bug: Bug Fixes\n" +
                    "- [`abc1234`](https://x/y) - update setup android action after google deprecation *(commit by @someone)*\n"
            )

        assertEquals(
            listOf("Update setup android action after google deprecation"),
            groups.single().messages,
        )
    }

    @Test
    fun `given the 1_29_0 release when parsed then its developer only features are dropped`() {
        val body =
            "### :sparkles: New Features\n" +
                "- [`5786364`](https://x/1) - **detekt**: integrate detekt *(PR #540 by @a)*\n" +
                "- [`b2c3d4e`](https://x/2) - **ci**: add stability checks *(PR #544 by @a)*\n"

        assertTrue(parser.parse(body).isEmpty())
    }

    @Test
    fun `given developer scopes next to user facing scopes when parsed then only user facing remain`() {
        val body =
            "### :bug: Bug Fixes\n" +
                "- [`1111111`](https://x/1) - **BUILD**: fix gradle *(PR #1 by @a)*\n" +
                "- [`2222222`](https://x/2) - **presentation**: fix crash *(PR #2 by @a)*\n" +
                "- [`3333333`](https://x/3) - **manga-list**: fix layout *(PR #3 by @a)*\n" +
                "- [`4444444`](https://x/4) - fix without scope *(PR #4 by @a)*\n"

        assertEquals(
            listOf("Fix crash", "Fix layout", "Fix without scope"),
            parser.parse(body).single().messages,
        )
    }

    @Test
    fun `given inline code in a message when parsed then the backticks are removed`() {
        val body =
            "### :bug: Bug Fixes\n" +
                "- [`abc1234`](https://x/y) - remove left over `.toExternalForm()` call *(commit by @a)*\n"

        assertEquals(
            listOf("Remove left over .toExternalForm() call"),
            parser.parse(body).single().messages,
        )
    }

    @Test
    fun `given an empty body when parsed then there are no groups`() {
        assertTrue(parser.parse("").isEmpty())
    }

    @Test
    fun `given entries without scope or pull request when parsed then the message is kept`() {
        val groups =
            parser.parse("### :bug: Bug Fixes\n- [`abc1234`](https://x/y) - fix the thing\n")

        assertEquals(listOf("Fix the thing"), groups.single().messages)
    }

    @Test
    fun `given windows line endings when parsed then entries are still found`() {
        val groups = parser.parse(RELEASE_1_32_4.replace("\n", "\r\n"))

        assertEquals(2, groups.single().messages.size)
    }

    @Test
    fun `given lines outside a known section when parsed then they are ignored`() {
        val groups = parser.parse("- [`abc1234`](https://x/y) - **data**: orphan *(PR #1 by @a)*\n")

        assertTrue(groups.isEmpty())
    }

    private companion object {
        val RELEASE_1_32_4 =
            """
            ### :bug: Bug Fixes
            - [`52e3a81`](https://github.com/spiderbiggen/manhwa-reader/commit/52e3a81ba8e90a0eedd3f0b6b7f9182c52806f6f) - **data**: sort manga overview by last chapter update *(PR #666 by @spiderbiggen)*
            - [`bcb7964`](https://github.com/spiderbiggen/manhwa-reader/commit/bcb7964268671a0a855da0588c896e52efa1c79a) - **data**: derive manga unread state from local chapters only *(PR #674 by @spiderbiggen)*

            ### :wrench: Chores
            """
                .trimIndent()

        val RELEASE_WITH_ALL_SECTIONS =
            """
            ### :sparkles: New Features
            - [`1111111`](https://x/1) - **presentation**: add update notice *(PR #1 by @a)*

            ### :bug: Bug Fixes
            - [`2222222`](https://x/2) - **app**: fix crash on launch *(PR #2 by @a)*

            ### :zap: Performance Improvements
            - [`3333333`](https://x/3) - **data**: cache cover images *(PR #3 by @a)*

            ### :recycle: Refactors
            - [`4444444`](https://x/4) - **data**: tidy *(PR #4 by @a)*

            ### :wrench: Chores
            - [`5555555`](https://x/5) - **deps**: bump *(PR #5 by @a)*
            """
                .trimIndent()
    }
}
