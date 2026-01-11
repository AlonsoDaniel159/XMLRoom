package com.alonso.xmlroom.room

object Constants {
    // Database
    const val DB_NAME = "db_insects_app"
    const val DB_VERSION = 1

    // Entity Names (Nombres de tablas)
    const val E_INSECTS = "insects_table"
    const val E_USERS = "users_table"

    // Insect Properties (Columnas de Insect)
    const val P_ID = "id"
    const val P_NAME = "name"
    const val P_IMG_LOCATION = "img_location"
    const val P_USER_ID = "user_id"

    // User Properties (Columnas de User)
    const val P_USER_ID_PK = "id"
    const val P_FIRST_NAME = "first_name"
    const val P_LAST_NAME = "last_name"
    const val P_EMAIL = "email"
    const val P_PIN = "pin"
}