// RUTA: app/src/main/java/com/alonso/xmlroom/ui/utils/ImagePickerHelper.kt
package com.alonso.xmlroom.utils

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagePickerHelper(
    private val activity: AppCompatActivity,
    private val onImageSelected: (Uri) -> Unit // Callback para devolver la URI seleccionada
) {
    private var tempImageUri: Uri? = null

    // Lanzadores de contratos de actividad (se registran una sola vez)
    // Registro del Photo Picker (Galería)
    private val pickMedia: ActivityResultLauncher<PickVisualMediaRequest> =
        activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let(onImageSelected)
        }
    private val takePicture: ActivityResultLauncher<Uri>
    private val requestCameraPermission: ActivityResultLauncher<String>

    init {

        // Registro del lanzador de la cámara
        takePicture = activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tempImageUri?.let(onImageSelected)
            }
        }

        // Registro del solicitante de permisos
        requestCameraPermission = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchCamera()
            }
        }
    }

    /** Muestra el diálogo para que el usuario elija entre Cámara y Galería. */
    fun selectImage() {
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(activity)
            .setTitle("Select image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> launchGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (activity.checkSelfPermission(android.Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun launchGallery() {
        pickMedia.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }


    private fun launchCamera() {
        tempImageUri = createImageUri()
        tempImageUri?.let { takePicture.launch(it) }
    }

    private fun createImageUri(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", activity.getExternalFilesDir("Pictures"))

        return FileProvider.getUriForFile(
            activity,
            "com.alonso.xmlroom.fileprovider", // ¡Debe coincidir con el Manifest!
            imageFile
        )
    }
}
