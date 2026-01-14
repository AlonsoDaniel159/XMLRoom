package com.alonso.xmlroom.utils

/**
 * Representa el estado de la UI de forma semántica.
 * Patrón recomendado por Google en Android moderno.
 */
sealed interface UiState<out T> {
    /**
     * Estado inicial o cargando datos
     */
    data object Loading: UiState<Nothing>

    /**
     * Datos cargados exitosamente
     * @param data Los datos a mostrar
     */
    data class Success<T>(val data: T) : UiState<T>

    /**
     * Error al cargar datos
     * @param message Mensaje de error para el usuario
     */
    data class Error(val message: String) : UiState<Nothing>
}