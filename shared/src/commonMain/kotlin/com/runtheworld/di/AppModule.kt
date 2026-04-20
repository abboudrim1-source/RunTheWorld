package com.runtheworld.di

import com.runtheworld.data.repository.KtorAuthRepositoryImpl
import com.runtheworld.data.repository.KtorFriendRepositoryImpl
import com.runtheworld.data.repository.KtorLeaderboardRepositoryImpl
import com.runtheworld.data.repository.KtorRemoteTerritoryRepositoryImpl
import com.runtheworld.data.repository.KtorRunSyncRepositoryImpl
import com.runtheworld.data.repository.KtorUserProfileRepositoryImpl
import com.runtheworld.data.repository.RunRepositoryImpl
import com.runtheworld.data.repository.TerritoryRepositoryImpl
import com.runtheworld.domain.repository.AuthRepository
import com.runtheworld.domain.repository.FriendRepository
import com.runtheworld.domain.repository.LeaderboardRepository
import com.runtheworld.domain.repository.RemoteTerritoryRepository
import com.runtheworld.domain.repository.RunRepository
import com.runtheworld.domain.repository.RunSyncRepository
import com.runtheworld.domain.repository.TerritoryRepository
import com.runtheworld.domain.repository.UserProfileRepository
import com.runtheworld.presentation.auth.AuthViewModel
import com.runtheworld.presentation.friends.FriendsViewModel
import com.runtheworld.presentation.history.HistoryViewModel
import com.runtheworld.presentation.leaderboard.LeaderboardViewModel
import com.runtheworld.presentation.map.MapViewModel
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.presentation.run.RunViewModel
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
}

val repositoryModule = module {
    single<RunRepository> { RunRepositoryImpl(get()) }
    single<TerritoryRepository> { TerritoryRepositoryImpl(get()) }
    single<RemoteTerritoryRepository> { KtorRemoteTerritoryRepositoryImpl(get(), get()) }
    single<UserProfileRepository> { KtorUserProfileRepositoryImpl(get(), get(), get()) }
    single<AuthRepository> { KtorAuthRepositoryImpl(get(), get()) }
    single<FriendRepository> { KtorFriendRepositoryImpl(get(), get()) }
    single<RunSyncRepository> { KtorRunSyncRepositoryImpl(get(), get()) }
    single<LeaderboardRepository> { KtorLeaderboardRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { RunViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { MapViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { FriendsViewModel(get()) }
    viewModel { LeaderboardViewModel(get()) }
}

fun appModules(): List<Module> = listOf(platformModule, networkModule, repositoryModule, viewModelModule)
