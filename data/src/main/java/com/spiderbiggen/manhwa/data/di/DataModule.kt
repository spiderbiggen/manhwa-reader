package com.spiderbiggen.manhwa.data.di

import com.spiderbiggen.manhwa.data.usecase.chapter.GetChapterImagesImpl
import com.spiderbiggen.manhwa.data.usecase.chapter.GetChapterImpl
import com.spiderbiggen.manhwa.data.usecase.chapter.GetChaptersImpl
import com.spiderbiggen.manhwa.data.usecase.chapter.GetSurroundingChaptersImpl
import com.spiderbiggen.manhwa.data.usecase.favorite.IsFavoriteImpl
import com.spiderbiggen.manhwa.data.usecase.favorite.ToggleFavoriteImpl
import com.spiderbiggen.manhwa.data.usecase.manga.GetActiveMangaImpl
import com.spiderbiggen.manhwa.data.usecase.manga.GetDroppedMangaImpl
import com.spiderbiggen.manhwa.data.usecase.manga.GetFavoriteMangaImpl
import com.spiderbiggen.manhwa.data.usecase.manga.GetMangaImpl
import com.spiderbiggen.manhwa.data.usecase.read.MangaIsReadImpl
import com.spiderbiggen.manhwa.data.usecase.read.IsReadImpl
import com.spiderbiggen.manhwa.data.usecase.read.SetReadImpl
import com.spiderbiggen.manhwa.data.usecase.read.SetReadUpToChapterImpl
import com.spiderbiggen.manhwa.data.usecase.read.ToggleReadImpl
import com.spiderbiggen.manhwa.data.usecase.remote.GetUpdatingStateImpl
import com.spiderbiggen.manhwa.data.usecase.remote.StartRemoteChapterUpdateImpl
import com.spiderbiggen.manhwa.data.usecase.remote.StartRemoteUpdateImpl
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manhwa.domain.usecase.manga.GetActiveManga
import com.spiderbiggen.manhwa.domain.usecase.manga.GetDroppedManga
import com.spiderbiggen.manhwa.domain.usecase.manga.GetFavoriteManga
import com.spiderbiggen.manhwa.domain.usecase.manga.GetManga
import com.spiderbiggen.manhwa.domain.usecase.read.MangaIsRead
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import com.spiderbiggen.manhwa.domain.usecase.read.SetRead
import com.spiderbiggen.manhwa.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manhwa.domain.usecase.read.ToggleRead
import com.spiderbiggen.manhwa.domain.usecase.remote.GetUpdatingState
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteChapterUpdate
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteUpdate
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindGetActiveManga(useCase: GetActiveMangaImpl): GetActiveManga

    @Binds
    abstract fun bindGetDroppedManga(useCase: GetDroppedMangaImpl): GetDroppedManga

    @Binds
    abstract fun bindGetFavoriteManga(useCase: GetFavoriteMangaImpl): GetFavoriteManga

    @Binds
    abstract fun bindGetManga(useCase: GetMangaImpl): GetManga

    @Binds
    abstract fun bindGetChapter(useCase: GetChapterImpl): GetChapter

    @Binds
    abstract fun bindGetSurroundingChapters(useCase: GetSurroundingChaptersImpl): GetSurroundingChapters

    @Binds
    abstract fun bindGetChapters(useCase: GetChaptersImpl): GetChapters

    @Binds
    abstract fun bindIsFavorite(useCase: IsFavoriteImpl): IsFavorite

    @Binds
    abstract fun bindGetChapterImages(useCase: GetChapterImagesImpl): GetChapterImages

    @Binds
    abstract fun bindToggleFavorite(useCase: ToggleFavoriteImpl): ToggleFavorite

    @Binds
    abstract fun bindIsMangaRead(useCase: MangaIsReadImpl): MangaIsRead

    @Binds
    abstract fun bindIsRead(useCase: IsReadImpl): IsRead

    @Binds
    abstract fun bindSetRead(useCase: SetReadImpl): SetRead

    @Binds
    abstract fun bindToggleRead(useCase: ToggleReadImpl): ToggleRead

    @Binds
    abstract fun bindSetReadUptoToChapter(useCase: SetReadUpToChapterImpl): SetReadUpToChapter

    @Binds
    abstract fun bindGetUpdatingState(useCase: GetUpdatingStateImpl): GetUpdatingState

    @Binds
    abstract fun bindStartRemoteUpdate(useCase: StartRemoteUpdateImpl): StartRemoteUpdate

    @Binds
    abstract fun bindStartRemoteChapterUpdate(useCase: StartRemoteChapterUpdateImpl): StartRemoteChapterUpdate
}