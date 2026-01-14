package com.alonso.xmlroom.room

import com.alonso.xmlroom.R
import com.alonso.xmlroom.room.entity.Insect
import com.alonso.xmlroom.room.entity.User
import com.alonso.xmlroom.room.entity.UserAuth
import com.alonso.xmlroom.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    fun getAllInsects(): Flow<UiState<List<Insect>>> = flow {
        emit(UiState.Loading)  // Emitir estado de carga

        try {
            // Observar cambios en la base de datos
            insectDao.getAllInsects().collect { insects ->
                    emit(UiState.Success(insects))  // Emitir datos
                }
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Error al cargar insectos"))  // Emitir error
        }
    }

    /**
     * Obtener insectos del usuario autenticado
     */
    fun getInsectsByUser(): Flow<UiState<List<Insect>>> = flow {
        emit(UiState.Loading)

        try {
            val userId = RoomApp.auth. id
            insectDao.getInsectsByUserId(userId).collect { insects ->
                    emit(UiState.Success(insects))
                }
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Error al cargar tus insectos"))
        }
    }

    /**
     * Agregar un insecto
     */
    suspend fun addInsect(insect:  Insect): Result<Long> = withContext(Dispatchers. IO) {
        runCatching {
            insect.userId = RoomApp.auth.id
            val newId = insectDao.addInsect(insect)
            if (newId > 0) {
                newId
            } else {
                throw Exception("No se pudo insertar el insecto")
            }
        }
    }

    /**
     * Eliminar un insecto
     */
    // --- Haz lo mismo para deleteInsect ---
    suspend fun deleteInsect(insect: Insect): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val deletedRows = insectDao.deleteInsect(insect)
            if (deletedRows == 0) {
                throw Exception("No se pudo eliminar el insecto")
            }
        }
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