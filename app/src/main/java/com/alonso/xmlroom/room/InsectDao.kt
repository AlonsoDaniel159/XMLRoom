package com.alonso.xmlroom.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.alonso.xmlroom.room.entity.Insect
import com.alonso.xmlroom.room.entity.UserWithInsects
import kotlinx.coroutines.flow.Flow

@Dao
interface InsectDao {

    // 📋 Obtener TODOS los insectos
    @Query("SELECT * FROM ${Constants.E_INSECTS}")
    fun getAllInsects(): Flow<List<Insect>>

    // 🔍 Obtener insectos de un usuario específico
    @Query("SELECT * FROM ${Constants.E_INSECTS} WHERE ${Constants.P_USER_ID} = :userId")
    fun getInsectsByUserId(userId: Long): Flow<List<Insect>>

    @Transaction // Es importante para que la consulta se haga de forma atómica
    @Query("SELECT * FROM ${Constants.E_USERS} WHERE id = :userId")
    fun getUserWithInsects(userId: Long): Flow<UserWithInsects>

    // ➕ Insertar UN insecto
    @Upsert
    suspend fun addInsect(insect: Insect): Long  // Retorna el ID generado

    // ❌ Eliminar un insecto
    @Delete
    suspend fun deleteInsect(insect: Insect): Int  // Retorna filas afectadas

    // 🗑️ Eliminar todos los insectos de un usuario
    @Query("DELETE FROM ${Constants.E_INSECTS} WHERE ${Constants.P_USER_ID} = :userId")
    suspend fun deleteInsectsByUserId(userId: Long): Int
}