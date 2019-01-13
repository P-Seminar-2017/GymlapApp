package de.gymnasium_lappersdorf.gymlapapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.*
import io.objectbox.BoxStore
import javax.inject.Singleton

@Module
class ObjectBoxModule {
    @Provides
    @Singleton
    fun provideBoxStore(context: Context): BoxStore = MyObjectBox
        .builder()
        .androidContext(context)
        .build()
}

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase() = DatabaseHandler()
}

@Component(modules = [ObjectBoxModule::class, DatabaseModule::class])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        @BindsInstance
        fun context(context: Context): Builder
    }

    fun inject(app: DatabaseHandler)
    fun inject(app: SubjectView)
    fun inject(app: LessonView)
    fun inject(app: DayFragment)
    fun inject(app: LessonActivity)

}