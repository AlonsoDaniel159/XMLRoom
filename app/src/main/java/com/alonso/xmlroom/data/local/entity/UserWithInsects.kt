package com.alonso.xmlroom.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithInsects(
    @Embedded
    val user: User, // El objeto principal

    @Relation(parentColumn = "id",       // La clave primaria del objeto principal (User)
        entityColumn = "user_id"   // La clave foránea en el objeto relacionado (Insect)
    )
    val insects: List<Insect> // La lista de objetos relacionados
)