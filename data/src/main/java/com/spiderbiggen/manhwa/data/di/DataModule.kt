package com.spiderbiggen.manhwa.data.di

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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindGetActiveManhwa(usecase: GetActiveManhwaImpl): GetActiveManhwa

    @Binds
    abstract fun bindGetDroppedManhwa(usecase: GetDroppedManhwaImpl): GetDroppedManhwa

    @Binds
    abstract fun bindGetFavoriteManhwa(usecase: GetFavoriteManhwaImpl): GetFavoriteManhwa

    @Binds
    abstract fun bindGetManhwa(usecase: GetManhwaImpl): GetManhwa

    @Binds
    abstract fun bindGetChapter(usecase: GetChapterImpl): GetChapter

    @Binds
    abstract fun bindGetSurroundingChapters(usecase: GetSurroundingChaptersImpl): GetSurroundingChapters

    @Binds
    abstract fun bindGetChapters(usecase: GetChaptersImpl): GetChapters

    @Binds
    abstract fun bindIsFavorite(usecase: IsFavoriteImpl): IsFavorite

    @Binds
    abstract fun bindUpdateChapters(usecase: UpdateChaptersImpl): UpdateChapters

    @Binds
    abstract fun bindUpdateManhwa(usecase: UpdateManhwaImpl): UpdateManhwa

    @Binds
    abstract fun bindGetChapterImages(usecase: GetChapterImagesImpl): GetChapterImages

    @Binds
    abstract fun bindToggleFavorite(usecase: ToggleFavoriteImpl): ToggleFavorite


}