package com.velkonost.upgrade.di

import com.velkonost.upgrade.App
import com.velkonost.upgrade.di.scope.AppScope
import com.velkonost.upgrade.push.di.ServiceBuilderModule
import com.velkonost.upgrade.repo.databases.UserSettingsDatabase
import com.velkonost.upgrade.rest.di.RetrofitModule
import com.velkonost.upgrade.ui.activity.main.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@AppScope
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        RetrofitModule::class,
        ServiceBuilderModule::class,
        AppModule::class,
        ReposModule::class,
        ViewModelFactoryModule::class,
        ViewModelModule::class,
        FragmentsModule::class,
        UserSettingsDatabase::class
    ]
)
interface AppComponent : AndroidInjector<App>