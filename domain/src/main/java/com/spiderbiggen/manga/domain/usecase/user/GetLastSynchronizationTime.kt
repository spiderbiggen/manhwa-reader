package com.spiderbiggen.manga.domain.usecase.user

import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

fun interface GetLastSynchronizationTime {
    operator fun invoke(): Flow<Instant?>
}
