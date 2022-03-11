package uz.gxteam.variantapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import uz.gxteam.variantapp.App
import uz.gxteam.variantapp.MainActivity
import uz.gxteam.variantapp.di.modules.DatabaseModule
import uz.gxteam.variantapp.di.modules.NetworModule
import uz.gxteam.variantapp.ui.SettingsFragment
import uz.gxteam.variantapp.ui.auth.AuthFragment
import uz.gxteam.variantapp.ui.chat.ChatFragment
import uz.gxteam.variantapp.ui.main.MainFragment
import uz.gxteam.variantapp.ui.main.mainView.MainViewFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class,NetworModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory{ fun create(@BindsInstance app: App, @BindsInstance context: Context):AppComponent }

    fun inject(mainActivity: MainActivity)
    fun inject(authFragment: AuthFragment)
    fun inject(chatFragment: ChatFragment)
    fun inject(mainFragment: MainFragment)
    fun inject(mainViewFragment: MainViewFragment)
    fun inject(fragmentSettings:SettingsFragment)
}