package com.spiderbiggen.manga.data.di

import com.spiderbiggen.manga.data.usecase.chapter.GetChapterImagesImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetChapterImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetChaptersImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetSurroundingChaptersImpl
import com.spiderbiggen.manga.data.usecase.chapter.UpdateChaptersFromRemoteImpl
import com.spiderbiggen.manga.data.usecase.favorite.HasFavoritesImpl
import com.spiderbiggen.manga.data.usecase.favorite.IsFavoriteImpl
import com.spiderbiggen.manga.data.usecase.favorite.ToggleFavoriteImpl
import com.spiderbiggen.manga.data.usecase.manga.GetActiveMangaImpl
import com.spiderbiggen.manga.data.usecase.manga.GetDroppedMangaImpl
import com.spiderbiggen.manga.data.usecase.manga.GetFavoriteMangaImpl
import com.spiderbiggen.manga.data.usecase.manga.GetMangaImpl
import com.spiderbiggen.manga.data.usecase.manga.UpdateMangaFromRemoteImpl
import com.spiderbiggen.manga.data.usecase.read.IsReadImpl
import com.spiderbiggen.manga.data.usecase.read.MangaIsReadImpl
import com.spiderbiggen.manga.data.usecase.read.SetReadImpl
import com.spiderbiggen.manga.data.usecase.read.SetReadUpToChapterImpl
import com.spiderbiggen.manga.data.usecase.read.ToggleReadImpl
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manga.domain.usecase.favorite.HasFavorites
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetActiveManga
import com.spiderbiggen.manga.domain.usecase.manga.GetDroppedManga
import com.spiderbiggen.manga.domain.usecase.manga.GetFavoriteManga
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import com.spiderbiggen.manga.domain.usecase.read.IsRead
import com.spiderbiggen.manga.domain.usecase.read.MangaIsRead
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manga.domain.usecase.read.ToggleRead
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {

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
    abstract fun bindHasFavorites(useCase: HasFavoritesImpl): HasFavorites

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
    abstract fun bindUpdateMangaFromRemote(useCase: UpdateMangaFromRemoteImpl): UpdateMangaFromRemote

    @Binds
    abstract fun bindUpdateChaptersFromRemote(useCase: UpdateChaptersFromRemoteImpl): UpdateChaptersFromRemote
}
