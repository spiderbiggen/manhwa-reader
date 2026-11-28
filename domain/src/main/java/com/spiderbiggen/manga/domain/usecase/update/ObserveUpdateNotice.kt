package com.spiderbiggen.manga.domain.usecase.update

import com.spiderbiggen.manga.domain.model.update.UpdateNotice
import kotlinx.coroutines.flow.Flow

/** Emits the notice to show, or null when the app is up to date or the notice was dismissed. */
fun interface ObserveUpdateNotice {
    operator fun invoke(): Flow<UpdateNotice?>
}
