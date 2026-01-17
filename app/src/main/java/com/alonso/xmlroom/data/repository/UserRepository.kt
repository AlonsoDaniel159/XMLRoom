package com.alonso.xmlroom.data.repository

import com.alonso.xmlroom.R
import com.alonso.xmlroom.data.local.RoomApp
import com.alonso.xmlroom.data.local.dao.TransactionDao
import com.alonso.xmlroom.data.local.dao.UserDao
import com.alonso.xmlroom.data.local.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class UserRepository {

    private val userDao: UserDao by lazy { RoomApp.db.userDao() }
    private val transactionDao: TransactionDao by lazy { RoomApp.db.transactionDao() }

    /**
     * Registrar nuevo usuario
     */
    suspend fun registerUser(user: User, onResult: (Boolean, Int) -> Unit) =
        withContext(Dispatchers.IO) {
            // Verificar si el email ya existe
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    onResult(false, R.string.register_error_is_registered)
                }
            } else {
                val newId = userDao.addUser(user)
                withContext(Dispatchers.Main) {
                    onResult(newId > 0, R.string.register_error_is_registered)
                }
            }
        }

    /**
     * Login
     */
    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            val user = userDao.getUserByEmail(email) ?: throw Exception("El usuario no existe")

            if(BCrypt.checkpw(password, user.password)) {
                user
            } else {
                throw Exception("Contraseña incorrecta")
            }
        }
    }

    /**
     * Obtener todos los usuarios
     */
    suspend fun getAllUsers(onResult: (List<User>) -> Unit) = withContext(Dispatchers.IO) {
        val users = userDao.getAllUsers()
        withContext(Dispatchers.Main) {
            onResult(users)
        }
    }

    // TRANSACCIONES

    /**
     * Eliminar usuario y todos sus datos
     */
    suspend fun deleteUserAndData(
        userId: Long,
        onResult: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            transactionDao.deleteUserAndData(userId)
            withContext(Dispatchers.Main) {
                onResult(true)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult(false)
            }
        }
    }
}