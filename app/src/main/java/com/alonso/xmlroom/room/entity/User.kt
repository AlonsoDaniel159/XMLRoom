package com.alonso.xmlroom.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alonso.xmlroom.room.Constants

@Entity(
    tableName = Constants.E_USERS,
    indices = [Index(value = [Constants.P_EMAIL], unique = true)] // 👈 Email único
)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.P_USER_ID_PK)
    val id: Long = 0,

    @ColumnInfo(name = Constants.P_FIRST_NAME)
    val firstName: String = "",

    @ColumnInfo(name = Constants.P_LAST_NAME)
    val lastName: String = "",

    @ColumnInfo(name = Constants.P_EMAIL)
    val email: String = "",

    @ColumnInfo(name = Constants.P_PIN)
    val pin: Int = 0
) {

}
