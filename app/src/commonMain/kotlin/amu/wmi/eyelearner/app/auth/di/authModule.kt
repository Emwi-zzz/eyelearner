package amu.wmi.eyelearner.app.auth.di

import amu.wmi.eyelearner.app.auth.data.local.SessionLocalDataSource
import amu.wmi.eyelearner.app.auth.data.local.SessionLocalDataSourceImpl
import amu.wmi.eyelearner.app.auth.data.remote.AuthRemoteDataSource
import amu.wmi.eyelearner.app.auth.data.remote.AuthRemoteDataSourceImpl
import amu.wmi.eyelearner.app.auth.data.repository.SessionRepositoryImpl
import amu.wmi.eyelearner.app.auth.domain.repository.SessionRepository
import amu.wmi.eyelearner.app.auth.domain.usecase.FinalizeLoginUseCase
import amu.wmi.eyelearner.app.auth.domain.usecase.GetSessionUseCase
import amu.wmi.eyelearner.app.auth.domain.usecase.InitLoginUseCase
import amu.wmi.eyelearner.app.auth.domain.usecase.LogoutUseCase
import amu.wmi.eyelearner.app.auth.domain.usecase.ObserveSessionUseCase
import amu.wmi.eyelearner.app.auth.domain.usecase.RefreshSessionUseCase
import amu.wmi.eyelearner.app.auth.presentation.login.LoginViewModel
import amu.wmi.eyelearner.app.auth.presentation.profile.ProfileViewModel
import amu.wmi.eyelearner.app.core.network.HttpClientFactory
import amu.wmi.eyelearner.app.core.platform.openUrl
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<HttpClient> { HttpClientFactory.create() }
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<SessionLocalDataSource> { SessionLocalDataSourceImpl() }
    single<SessionRepository> { SessionRepositoryImpl(get(), get()) }

    factory<InitLoginUseCase> { InitLoginUseCase(get()) }
    factory<FinalizeLoginUseCase> { FinalizeLoginUseCase(get()) }
    factory<GetSessionUseCase> { GetSessionUseCase(get()) }
    factory<ObserveSessionUseCase> { ObserveSessionUseCase(get()) }
    factory<LogoutUseCase> { LogoutUseCase(get()) }
    factory<RefreshSessionUseCase> { RefreshSessionUseCase(get()) }

    viewModel<LoginViewModel> {
        LoginViewModel(
            initLoginUseCase = get(),
            finalizeLoginUseCase = get(),
            observeSessionUseCase = get(),
            onOpenUrl = { url -> openUrl(url) }
        )
    }

    viewModel<ProfileViewModel> {
        ProfileViewModel(
            observeSessionUseCase = get(),
            logoutUseCase = get()
        )
    }
}