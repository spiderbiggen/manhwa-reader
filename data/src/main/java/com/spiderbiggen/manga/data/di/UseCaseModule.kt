package com.spiderbiggen.manga.data.di

import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteChaptersUseCase
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteMangaUseCase
import com.spiderbiggen.manga.data.source.remote.usecase.ResetBearerToken
import com.spiderbiggen.manga.data.usecase.auth.LoginImpl
import com.spiderbiggen.manga.data.usecase.auth.LogoutImpl
import com.spiderbiggen.manga.data.usecase.auth.RefreshAccessToken
import com.spiderbiggen.manga.data.usecase.auth.RegisterImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetChapterImagesImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetChapterImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetOverviewChaptersImpl
import com.spiderbiggen.manga.data.usecase.chapter.GetSurroundingChaptersImpl
import com.spiderbiggen.manga.data.usecase.chapter.UpdateChaptersFromRemoteImpl
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToDomainChapterUseCase
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToLocalChapterUseCase
import com.spiderbiggen.manga.data.usecase.favorite.IsFavoriteFlowImpl
import com.spiderbiggen.manga.data.usecase.favorite.ToggleFavoriteImpl
import com.spiderbiggen.manga.data.usecase.image.DecodeAvatarBitmap
import com.spiderbiggen.manga.data.usecase.image.EncodeBitmap
import com.spiderbiggen.manga.data.usecase.manga.GetMangaImpl
import com.spiderbiggen.manga.data.usecase.manga.GetOverviewMangaImpl
import com.spiderbiggen.manga.data.usecase.manga.UpdateMangaFromRemoteImpl
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToDomainMangaUseCase
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToLocalMangaUseCase
import com.spiderbiggen.manga.data.usecase.read.IsReadImpl
import com.spiderbiggen.manga.data.usecase.read.SetReadImpl
import com.spiderbiggen.manga.data.usecase.read.SetReadUpToChapterImpl
import com.spiderbiggen.manga.data.usecase.read.ToggleReadImpl
import com.spiderbiggen.manga.data.usecase.remote.UpdateStateFromRemoteImpl
import com.spiderbiggen.manga.data.usecase.user.GetLastSynchronizationTimeImpl
import com.spiderbiggen.manga.data.usecase.user.GetUserImpl
import com.spiderbiggen.manga.data.usecase.user.MapUserEntity
import com.spiderbiggen.manga.data.usecase.user.SynchronizeWithRemoteImpl
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
import com.spiderbiggen.manga.domain.usecase.remote.UpdateStateFromRemote
import com.spiderbiggen.manga.domain.usecase.user.GetLastSynchronizationTime
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote
import com.spiderbiggen.manga.domain.usecase.user.profile.UpdateAvatar
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
    // UseCases
    singleOf(::LoginImpl) { bind<Login>() }
    singleOf(::RegisterImpl) { bind<Register>() }
    singleOf(::LogoutImpl) { bind<Logout>() }
    singleOf(::GetLastSynchronizationTimeImpl) { bind<GetLastSynchronizationTime>() }
    singleOf(::GetUserImpl) { bind<GetUser>() }
    singleOf(::SynchronizeWithRemoteImpl) { bind<SynchronizeWithRemote>() }
    singleOf(::UpdateAvatarImpl) { bind<UpdateAvatar>() }
    singleOf(::GetOverviewMangaImpl) { bind<GetOverviewManga>() }
    singleOf(::GetMangaImpl) { bind<GetManga>() }
    singleOf(::GetChapterImpl) { bind<GetChapter>() }
    singleOf(::GetSurroundingChaptersImpl) { bind<GetSurroundingChapters>() }
    singleOf(::GetOverviewChaptersImpl) { bind<GetOverviewChapters>() }
    singleOf(::IsFavoriteFlowImpl) { bind<IsFavoriteFlow>() }
    single { GetChapterImagesImpl(get(named<BaseUrl>()), get()) } bind GetChapterImages::class
    singleOf(::ToggleFavoriteImpl) { bind<ToggleFavorite>() }
    singleOf(::IsReadImpl) { bind<IsReadFlow>() }
    singleOf(::SetReadImpl) { bind<SetRead>() }
    singleOf(::ToggleReadImpl) { bind<ToggleRead>() }
    singleOf(::SetReadUpToChapterImpl) { bind<SetReadUpToChapter>() }
    singleOf(::UpdateStateFromRemoteImpl) { bind<UpdateStateFromRemote>() }
    singleOf(::UpdateMangaFromRemoteImpl) { bind<UpdateMangaFromRemote>() }
    singleOf(::UpdateChaptersFromRemoteImpl) { bind<UpdateChaptersFromRemote>() }

    factoryOf(::FetchCurrentUser)
    factoryOf(::ResetBearerToken)
    factory { MapUserEntity(get(named<BaseUrl>())) }
    factoryOf(::RefreshAccessToken)
    factoryOf(::GetRemoteChaptersUseCase)
    factoryOf(::GetRemoteMangaUseCase)
    factoryOf(::ToLocalChapterUseCase)
    factoryOf(::ToDomainChapterUseCase)
    factoryOf(::ToLocalMangaUseCase)
    factoryOf(::ToDomainMangaUseCase)
    factoryOf(::DecodeAvatarBitmap)
    factoryOf(::EncodeBitmap)
}
