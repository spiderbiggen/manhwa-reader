package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.IsReadFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IsReadImpl @Inject constructor(private val readRepository: ReadRepository) : IsReadFlow {
    override fun invoke(id: ChapterId): Flow<Boolean> = readRepository.getFlow(id).map { it == true }
}
