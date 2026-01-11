package com.alonso.xmlroom.room

import android.app.Application
import com.alonso.xmlroom.room.entity.UserAuth

/**
 * Application class
 * Se ejecuta ANTES que cualquier Activity
 * Perfecto para inicializar Room
 */
class RoomApp: Application() {

    companion object {
        lateinit var db: AppDataBase
        lateinit var auth: UserAuth
    }

    override fun onCreate() {
        super.onCreate()
        db = AppDataBase.getDatabase(this)
    }
}