package com.alonso.xmlroom.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.alonso.xmlroom.data.preferences.UserPreferences
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ PRIMERO: Instalar Splash nativo (ANTES de super.onCreate)
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // ✅ NO llamar setContentView (el sistema maneja la UI)

        // Opcional: Mantener el splash visible mientras verificas
        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        // Verificar sesión y redirigir
        lifecycleScope.launch {
            val userId = UserPreferences(this@SplashActivity).getUserId()
            val intent = if (userId != null) {
                Intent(this@SplashActivity, InsectsActivity::class. java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }

            keepSplashOnScreen = false

            startActivity(intent)
            finish()
        }
    }
}