package com.alonso.xmlroom.room.entity

import androidx.room.ColumnInfo
import com.alonso.xmlroom.room.Constants

data class UserAuth(
    val id: Long,
    @ColumnInfo(name = Constants. P_FIRST_NAME)
    val name: String
)
