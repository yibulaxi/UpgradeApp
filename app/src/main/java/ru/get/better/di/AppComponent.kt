package ru.get.better.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.get.better.App
import ru.get.better.di.scope.AppScope
import ru.get.better.push.di.ServiceBuilderModule
import ru.get.better.rest.di.RetrofitModule
import ru.get.better.ui.activity.main.ViewModelModule

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
    ]
)
interface AppComponent : AndroidInjector<App>