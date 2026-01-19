package com.spiderbiggen.manga.data.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import coil3.SingletonImageLoader
import com.spiderbiggen.manga.data.BuildConfig
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.source.local.room.MangaDatabase
import com.spiderbiggen.manga.data.source.local.room.MangaDatabaseDecorator
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.impl.AuthServiceImpl
import com.spiderbiggen.manga.data.source.remote.impl.MangaServiceImpl
import com.spiderbiggen.manga.data.source.remote.impl.UserServiceImpl
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.source.remote.model.auth.SessionResponse
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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    single(qualifier = named<BaseUrl>()) { "https://manga.spiderbiggen.com/" }

    single { SingletonImageLoader.get(androidContext()) }

    single {
        val baseUrl: String = get(qualifier = named<BaseUrl>())
        val json: Json = get()
        val authRepository = get<AuthenticationRepository>()
        HttpClient(OkHttp) {
            val host = Url(baseUrl).host

            expectSuccess = true

            defaultRequest {
                url(baseUrl)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpCache) {
                val cacheFile = androidContext().cacheDir.resolve("http-cache")
                publicStorage(FileStorage(cacheFile))
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 5_000
                socketTimeoutMillis = 5_000
            }

            install(ContentEncoding) {
                gzip(1.0F)
                deflate(0.9F)
            }


            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("HttpClient", message)
                    }
                }
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.INFO
                sanitizeHeader { it == HttpHeaders.Authorization }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        authRepository.getAuthenticatedState()?.let {
                            BearerTokens(it.accessToken.token, it.refreshToken.token)
                        }
                    }
                    refreshTokens {
                        val refreshToken = oldTokens?.refreshToken ?: return@refreshTokens null
                        val response = client.post("api/v1/auth/refresh") {
                            setBody(RefreshTokenBody(refreshToken))
                            markAsRefreshTokenRequest()
                        }
                        if (response.status == HttpStatusCode.OK) {
                            val sessionResponse: SessionResponse = response.body()
                            authRepository.saveTokens(
                                sessionResponse.accessToken,
                                sessionResponse.refreshToken,
                            )
                            BearerTokens(
                                accessToken = sessionResponse.accessToken.token,
                                refreshToken = sessionResponse.refreshToken.token,
                            )
                        } else {
                            null
                        }
                    }
                    sendWithoutRequest { request ->
                        request.url.host == host && !request.url.encodedPath.contains("/auth")
                    }
                }
            }
        }
    }

    single<MangaService> { MangaServiceImpl(get()) }
    single<AuthService> { AuthServiceImpl(get()) }
    single<UserService> { UserServiceImpl(get()) }

    single { androidContext().getSharedPreferences("manga", Context.MODE_PRIVATE) }

    single {
        MangaDatabaseDecorator(
            Room.databaseBuilder(androidContext(), MangaDatabase::class.java, "manga")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build(),
        )
    }

    single { get<MangaDatabaseDecorator>().localMangaDao() }
    single { get<MangaDatabaseDecorator>().localChapterDao() }
    single { get<MangaDatabaseDecorator>().mangaFavoriteStatusDao() }
    single { get<MangaDatabaseDecorator>().chapterReadStatusDao() }
    single { androidContext().contentResolver }

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

    // Repositories
    singleOf(::AuthenticationRepository)
    singleOf(::FavoritesRepository)
    singleOf(::ReadRepository)
    singleOf(::MangaRepository)
    singleOf(::ChapterRepository)
}
