package com.spiderbiggen.manga.data.di

import com.spiderbiggen.manga.data.usecase.auth.LoginImpl
import com.spiderbiggen.manga.data.usecase.auth.LogoutImpl
import com.spiderbiggen.manga.data.usecase.auth.RegisterImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetChapterImagesImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetChapterImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetOverviewChaptersImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetSurroundingChaptersImpl
import com.spiderbiggen.manga.data.usecase.chapter.UpdateChaptersFromRemoteImpl
import com.spiderbiggen.manga.data.usecase.favorite.IsFavoriteFlowImpl
import com.spiderbiggen.manga.data.usecase.favorite.ToggleFavoriteImpl
import com.spiderbiggen.manga.data.usecase.manga.GetMangaImpl
import com.spiderbiggen.manga.data.usecase.manga.GetOverviewMangaImpl
import com.spiderbiggen.manga.data.usecase.manga.UpdateMangaFromRemoteImpl
import com.spiderbiggen.manga.data.usecase.read.IsReadImpl
import com.spiderbiggen.manga.data.usecase.read.SetReadImpl
import com.spiderbiggen.manga.data.usecase.read.SetReadUpToChapterImpl
import com.spiderbiggen.manga.data.usecase.read.ToggleReadImpl
import com.spiderbiggen.manga.data.usecase.user.GetUserImpl
import com.spiderbiggen.manga.data.usecase.user.profile.UpdateAvatarImpl
import com.spiderbiggen.manga.domain.usecase.auth.Login
import com.spiderbiggen.manga.domain.usecase.auth.Logout
import com.spiderbiggen.manga.domain.usecase.auth.Register
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manga.domain.usecase.chapter.GetOverviewChapters
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavoriteFlow
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import com.spiderbiggen.manga.domain.usecase.manga.GetOverviewManga
import com.spiderbiggen.manga.domain.usecase.read.IsReadFlow
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manga.domain.usecase.read.ToggleRead
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import com.spiderbiggen.manga.domain.usecase.user.profile.UpdateAvatar
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {

    @Binds
    abstract fun bindLogin(useCase: LoginImpl): Login

    @Binds
    abstract fun bindRegister(useCase: RegisterImpl): Register

    @Binds
    abstract fun bindLogout(useCase: LogoutImpl): Logout

    @Binds
    abstract fun bindGetUser(useCase: GetUserImpl): GetUser

    @Binds
    abstract fun bindUpdateAvatar(useCase: UpdateAvatarImpl): UpdateAvatar

    @Binds
    abstract fun bindGetOverviewManga(useCase: GetOverviewMangaImpl): GetOverviewManga

    @Binds
    abstract fun bindGetManga(useCase: GetMangaImpl): GetManga

    @Binds
    abstract fun bindGetChapter(useCase: GetChapterImpl): GetChapter

    @Binds
    abstract fun bindGetSurroundingChapters(useCase: GetSurroundingChaptersImpl): GetSurroundingChapters

    @Binds
    abstract fun bindGetChapters(useCase: GetOverviewChaptersImpl): GetOverviewChapters

    @Binds
    abstract fun bindIsFavoriteFlow(useCase: IsFavoriteFlowImpl): IsFavoriteFlow

    @Binds
    abstract fun bindGetChapterImages(useCase: GetChapterImagesImpl): GetChapterImages

    @Binds
    abstract fun bindToggleFavorite(useCase: ToggleFavoriteImpl): ToggleFavorite

    @Binds
    abstract fun bindIsReadFlow(useCase: IsReadImpl): IsReadFlow

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
