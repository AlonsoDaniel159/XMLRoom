package com.alonso.xmlroom.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alonso.xmlroom.databinding.ActivityLoginBinding
import com.alonso.xmlroom.ui.viewmodels.LoginViewModel
import com.alonso.xmlroom.ui.viewmodels.LoginViewModelFactory
import com.alonso.xmlroom.data.preferences.UserPreferences
import com.alonso.xmlroom.data.repository.UserRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.getValue

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels{
        LoginViewModelFactory(UserPreferences(this), UserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()  // 👈 CLAVE: Observar los LiveData
        setupListeners()
    }

    private fun setupObservers() {
        // Observar lista de insectos

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observar mensajes (toasts)
                launch {
                    viewModel.message.collect { message ->
                        errorLogin(message)
                    }
                }

                launch {
                    viewModel.navigateToHome.collect {
                        val intent = Intent(this@LoginActivity, InsectsActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }


    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val intent = Intent(this, RegisterActivity::class.java)

            if (email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                intent.putExtra("email", email)
            }

            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener { // Asumo que es el botón de login
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            binding.tilEmail.error = null
            binding.tilPassword.error = null

            // 2. Realizamos las validaciones de UI
            if (email.isBlank()) {
                binding.tilEmail.error = "El email no puede estar vacío"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Formato de email inválido"
                return@setOnClickListener
            }

            if (password.isBlank()) {
                binding.tilPassword.error = "La contraseña no puede estar vacía"
                return@setOnClickListener
            }

            // 3. Si todo está bien, delegamos al ViewModel
            viewModel.login(email, password)

        }
    }

    private fun errorLogin(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

}