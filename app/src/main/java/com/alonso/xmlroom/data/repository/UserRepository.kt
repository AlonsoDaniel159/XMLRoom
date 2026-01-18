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
    suspend fun registerUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            if (userDao.getUserByEmail(user.email) != null) {
                throw Exception("Ya existe un usuario con ese email")
            }

            val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
            val userToSave = user.copy(password = hashedPassword)

            if (userDao.addUser(userToSave) <= 0) {
                throw Exception("No se pudo insertar el usuario")
            }
            user
        }
    }

    /**
     * Login
     */
    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            val user = userDao.getUserByEmail(email) ?: throw Exception("El usuario no existe")

            if (!BCrypt.checkpw(password, user.password)) {
                throw Exception("Contraseña incorrecta")
            }
            user
        }
    }

    // TRANSACCIONES

    suspend fun deleteUserAndData(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            transactionDao.deleteUserAndData(userId)
        }
    }
}