package com.alonso.xmlroom.data.repository

import com.alonso.xmlroom.data.local.RoomApp
import com.alonso.xmlroom.data.local.dao.InsectDao
import com.alonso.xmlroom.data.local.entity.Insect
import com.alonso.xmlroom.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class InsectRepository {

    private val insectDao: InsectDao by lazy { RoomApp.db.insectDao() }


    /**
     * Obtener todos los insectos
     */
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
    fun getInsectsByUser(userId: Long): Flow<UiState<List<Insect>>> = flow {
        emit(UiState.Loading)

        try {
            val userId = userId
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
    suspend fun addInsect(insect: Insect): Result<Long> = withContext(Dispatchers.IO) {
        runCatching {
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
}