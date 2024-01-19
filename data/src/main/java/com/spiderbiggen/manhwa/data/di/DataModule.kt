package com.spiderbiggen.manhwa.data.di

import com.spiderbiggen.manhwa.data.usecase.StartRemoteUpdateImpl
import com.spiderbiggen.manhwa.data.usecase.chapter.GetChapterImagesImpl
import com.spiderbiggen.manhwa.data.usecase.chapter.GetChapterImpl
import com.spiderbiggen.manhwa.data.usecase.chapter.GetChaptersImpl
import com.spiderbiggen.manhwa.data.usecase.chapter.GetSurroundingChaptersImpl
import com.spiderbiggen.manhwa.data.usecase.chapter.UpdateChaptersImpl
import com.spiderbiggen.manhwa.data.usecase.favorite.IsFavoriteImpl
import com.spiderbiggen.manhwa.data.usecase.favorite.ToggleFavoriteImpl
import com.spiderbiggen.manhwa.data.usecase.manhwa.GetActiveManhwaImpl
import com.spiderbiggen.manhwa.data.usecase.manhwa.GetDroppedManhwaImpl
import com.spiderbiggen.manhwa.data.usecase.manhwa.GetFavoriteManhwaImpl
import com.spiderbiggen.manhwa.data.usecase.manhwa.GetManhwaImpl
import com.spiderbiggen.manhwa.data.usecase.manhwa.UpdateManhwaImpl
import com.spiderbiggen.manhwa.data.usecase.read.IsManhwaReadImpl
import com.spiderbiggen.manhwa.data.usecase.read.IsReadImpl
import com.spiderbiggen.manhwa.data.usecase.read.SetReadImpl
import com.spiderbiggen.manhwa.data.usecase.read.SetReadUpToChapterImpl
import com.spiderbiggen.manhwa.data.usecase.read.ToggleReadImpl
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteUpdate
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manhwa.domain.usecase.chapter.UpdateChapters
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetActiveManhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetDroppedManhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetFavoriteManhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetManhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.UpdateManhwa
import com.spiderbiggen.manhwa.domain.usecase.read.IsManhwaRead
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import com.spiderbiggen.manhwa.domain.usecase.read.SetRead
import com.spiderbiggen.manhwa.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manhwa.domain.usecase.read.ToggleRead
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindGetActiveManhwa(useCase: GetActiveManhwaImpl): GetActiveManhwa

    @Binds
    abstract fun bindGetDroppedManhwa(useCase: GetDroppedManhwaImpl): GetDroppedManhwa

    @Binds
    abstract fun bindGetFavoriteManhwa(useCase: GetFavoriteManhwaImpl): GetFavoriteManhwa

    @Binds
    abstract fun bindGetManhwa(useCase: GetManhwaImpl): GetManhwa

    @Binds
    abstract fun bindGetChapter(useCase: GetChapterImpl): GetChapter

    @Binds
    abstract fun bindGetSurroundingChapters(useCase: GetSurroundingChaptersImpl): GetSurroundingChapters

    @Binds
    abstract fun bindGetChapters(useCase: GetChaptersImpl): GetChapters

    @Binds
    abstract fun bindIsFavorite(useCase: IsFavoriteImpl): IsFavorite

    @Binds
    abstract fun bindUpdateChapters(useCase: UpdateChaptersImpl): UpdateChapters

    @Binds
    abstract fun bindUpdateManhwa(useCase: UpdateManhwaImpl): UpdateManhwa

    @Binds
    abstract fun bindGetChapterImages(useCase: GetChapterImagesImpl): GetChapterImages

    @Binds
    abstract fun bindToggleFavorite(useCase: ToggleFavoriteImpl): ToggleFavorite

    @Binds
    abstract fun bindIsManhwaRead(useCase: IsManhwaReadImpl): IsManhwaRead

    @Binds
    abstract fun bindIsRead(useCase: IsReadImpl): IsRead

    @Binds
    abstract fun bindSetRead(useCase: SetReadImpl): SetRead

    @Binds
    abstract fun bindToggleRead(useCase: ToggleReadImpl): ToggleRead

    @Binds
    abstract fun bindSetReadUptToChapter(useCase: SetReadUpToChapterImpl): SetReadUpToChapter

    @Binds
    abstract fun bindStartRemoteUpdate(useCase: StartRemoteUpdateImpl): StartRemoteUpdate
}