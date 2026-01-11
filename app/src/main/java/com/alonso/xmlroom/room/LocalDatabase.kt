package com.alonso.xmlroom.room

import com.alonso.xmlroom.R
import com.alonso.xmlroom.room.entity.Insect
import com.alonso.xmlroom.room.entity.User
import com.alonso.xmlroom.room.entity.UserAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Clase helper que simplifica el acceso a la BD
 * Maneja hilos y callbacks de forma elegante
 */
class LocalDatabase {

    // DAOs
    private val insectDao: InsectDao by lazy { RoomApp.db.insectDao() }
    private val userDao: UserDao by lazy { RoomApp.db.userDao() }
    private val transactionDao: TransactionDao by lazy { RoomApp.db.transactionDao() }

    // ═══════════════════════════════════════════════════════
    // OPERACIONES DE INSECTOS
    // ═══════════════════════════════════════════════════════

    /**
     * Obtener todos los insectos
     */
    // Esta función ya no necesita ser 'suspend'. Simplemente devuelve el Flow de Room.
    fun getAllInsects(): Flow<List<Insect>> {
        return insectDao.getAllInsects() // Suponiendo que tienes un metodo así en tu DAO
    }

    /**
     * Obtener insectos del usuario autenticado
     */
    fun getMyInsects(): Flow<List<Insect>> {
        val userId = RoomApp.auth.id
        return insectDao.getInsectByUserId(userId)
    }

    /**
     * Agregar un insecto
     */
    suspend fun addInsect(insect: Insect): Boolean = withContext(Dispatchers.IO) {
        insect.userId = RoomApp.auth.id
        val newId = insectDao.addInsect(insect)
        return@withContext newId > 0 // Esto devuelve true o false
    }

    /**
     * Eliminar un insecto
     */
    // --- Haz lo mismo para deleteInsect ---
    suspend fun deleteInsect(insect: Insect) = withContext(Dispatchers.IO) {
        val deletedRows = insectDao.deleteInsect(insect)
        return@withContext deletedRows > 0
    }

    // ═══════════════════════════════════════════════════════
    // OPERACIONES DE USUARIOS
    // ═══════════════════════════════════════════════════════

    /**
     * Registrar nuevo usuario
     */
    suspend fun registerUser(
        user: User,
        onResult: (Boolean, Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        // Verificar si el email ya existe
        val existingUser = userDao.findUserByEmail(user.email)
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
    suspend fun login(
        email: String,
        pin: Int,
        onResult: (UserAuth?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val user = userDao.login(email, pin)
        withContext(Dispatchers.Main) {
            onResult(user)
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

    // ═══════════════════════════════════════════════════════
    // TRANSACCIONES
    // ═══════════════════════════════════════════════════════

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