package com.runtheworld.di

import com.runtheworld.data.repository.RunRepositoryImpl
import com.runtheworld.data.repository.TerritoryRepositoryImpl
import com.runtheworld.data.repository.UserProfileRepositoryImpl
import com.runtheworld.domain.repository.RunRepository
import com.runtheworld.domain.repository.TerritoryRepository
import com.runtheworld.domain.repository.UserProfileRepository
import com.runtheworld.presentation.history.HistoryViewModel
import com.runtheworld.presentation.map.MapViewModel
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.presentation.run.RunViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Declared as expect so each platform can supply its own module (DB driver, LocationService, Settings). */
expect val platformModule: Module

val repositoryModule = module {
    single<RunRepository> { RunRepositoryImpl(get()) }
    single<TerritoryRepository> { TerritoryRepositoryImpl(get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { ProfileViewModel(get()) }
    viewModel { RunViewModel(get(), get(), get(), get()) }
    viewModel { MapViewModel(get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
}

/** Call from platform entry point (Application.onCreate on Android, init() in Swift on iOS). */
fun appModules(): List<Module> = listOf(platformModule, repositoryModule, viewModelModule)
