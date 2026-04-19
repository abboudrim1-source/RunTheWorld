package com.runtheworld.di

import com.runtheworld.data.repository.FriendRepositoryImpl
import com.runtheworld.data.repository.KtorUserProfileRepositoryImpl
import com.runtheworld.data.repository.RoomAuthRepositoryImpl
import com.runtheworld.data.repository.RunRepositoryImpl
import com.runtheworld.data.repository.TerritoryRepositoryImpl
import com.runtheworld.domain.repository.AuthRepository
import com.runtheworld.domain.repository.FriendRepository
import com.runtheworld.domain.repository.RunRepository
import com.runtheworld.domain.repository.TerritoryRepository
import com.runtheworld.domain.repository.UserProfileRepository
import com.runtheworld.presentation.auth.AuthViewModel
import com.runtheworld.presentation.friends.FriendsViewModel
import com.runtheworld.presentation.history.HistoryViewModel
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

/** Declared as expect so each platform can supply its own module (DB driver, LocationService, Settings). */
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
    single<UserProfileRepository> { KtorUserProfileRepositoryImpl(get(), get(), get()) }
    single<AuthRepository> { RoomAuthRepositoryImpl(get(), get()) }
    single<FriendRepository> { FriendRepositoryImpl(get(), get(), get()) }  // FriendRequestDao, UserProfileDao, Settings
}

val viewModelModule = module {
    viewModel { ProfileViewModel(get()) }
    viewModel { RunViewModel(get(), get(), get(), get()) }
    viewModel { MapViewModel(get(), get(), get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { FriendsViewModel(get()) }
}

/** Call from platform entry point (Application.onCreate on Android, init() in Swift on iOS). */
fun appModules(): List<Module> = listOf(platformModule, networkModule, repositoryModule, viewModelModule)
