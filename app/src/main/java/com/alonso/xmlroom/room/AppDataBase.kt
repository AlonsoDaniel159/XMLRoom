package com.alonso.xmlroom.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alonso.xmlroom.room.entity.Insect
import com.alonso.xmlroom.room.entity.User

/**
 * Base de datos principal
 * Contiene todas las entidades y DAOs
 */
@Database(
    entities = [Insect::class, User::class],  // 👈 Lista de entidades
    version = Constants.DB_VERSION,
    exportSchema = false  // Para development, cambiar a true en producción
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun insectDao(): InsectDao
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null
        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    Constants.DB_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}