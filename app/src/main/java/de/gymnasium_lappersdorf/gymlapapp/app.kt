package de.gymnasium_lappersdorf.gymlapapp

import android.app.Application
import de.gymnasium_lappersdorf.gymlapapp.di.AppComponent
import de.gymnasium_lappersdorf.gymlapapp.di.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        //building dependency graph
        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()
    }
}