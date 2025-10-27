package com.example.appmovile

import android.app.Application
import com.example.appmovile.di.AppContainer
import com.example.appmovile.di.AppDataContainer

class AppMovileApp : Application() {
    // Inicializa el contenedor para que sea accesible globalmente
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}