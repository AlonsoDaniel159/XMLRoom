package com.alonso.xmlroom.data.local

import android.app.Application

/**
 * Application class
 * Se ejecuta ANTES que cualquier Activity
 * Perfecto para inicializar Room
 */
class RoomApp: Application() {

    companion object {
        lateinit var db: AppDataBase
    }

    override fun onCreate() {
        super.onCreate()
        db = AppDataBase.getDatabase(this)
    }
}