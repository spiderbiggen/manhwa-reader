package com.spiderbiggen.manga.data.usecase.update

import arrow.core.Either
import com.spiderbiggen.manga.data.source.local.repository.UpdateStore
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.usecase.update.DismissUpdateNotice

class DismissUpdateNoticeImpl(private val store: UpdateStore) : DismissUpdateNotice {
    override suspend fun invoke(): Either<AppError, Unit> = store.dismiss()
}
