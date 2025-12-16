package com.spiderbiggen.manga.presentation.ui.profile.overview

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.auth.Logout
import com.spiderbiggen.manga.domain.usecase.user.GetLastSynchronizationTime
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote
import com.spiderbiggen.manga.domain.usecase.user.profile.UpdateAvatar
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.suspended
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URI
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

private val localDateTimeFormat = LocalDateTime.Format {
    dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    year(Padding.NONE)
    char('\t')
    hour()
    char(':')
    minute()
}

@HiltViewModel
class ProfileOverviewViewModel @Inject constructor(
    private val getUser: GetUser,
    private val getLastSynchronizationTime: GetLastSynchronizationTime,
    private val logout: Logout,
    private val updateAvatar: UpdateAvatar,
    private val synchronizeWithRemote: SynchronizeWithRemote,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    private val _snackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = _snackbarFlow.asSharedFlow()

    val state: StateFlow<ProfileOverviewViewState> = screenStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileOverviewViewState.Unknown,
        )

    fun screenStateFlow() = combine(
        getUser(),
        getLastSynchronizationTime(),
    ) { user, lastSynchronizationTime ->
        if (user == null) {
            ProfileOverviewViewState.Unauthenticated
        } else {
            ProfileOverviewViewState.Authenticated(
                id = user.id,
                name = user.username,
                avatarUrl = user.avatarUrl,
                email = user.email,
                updatedAt = user.updatedAt,
                lastSynchronizationTime = lastSynchronizationTime
                    ?.toLocalDateTime(TimeZone.currentSystemDefault())
                    ?.format(localDateTimeFormat),
            )
        }
    }

    fun handleLogout() = suspended {
        logout().leftOrElse {
            _snackbarFlow.emit(SnackbarData(formatAppError(it)))
        }
    }

    fun handleChangeAvatar(uri: Uri) = suspended {
        updateAvatar(URI.create(uri.toString())).leftOrElse {
            _snackbarFlow.emit(SnackbarData(formatAppError(it)))
        }
    }

    fun handleSync() = suspended {
        synchronizeWithRemote(ignoreInterval = false).leftOrElse {
            _snackbarFlow.emit(SnackbarData(formatAppError(it)))
        }
    }
}
