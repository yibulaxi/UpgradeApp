package com.velkonost.upgrade.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.velkonost.upgrade.App
import com.velkonost.upgrade.di.scope.AppScope
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(
    includes = []
)

class AppModule(val app: App) {
    @Provides
    @AppScope
    fun context(): Context = app
}

@Module(includes = [])
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}