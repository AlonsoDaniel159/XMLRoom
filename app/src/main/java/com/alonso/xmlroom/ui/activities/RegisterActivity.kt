package com.alonso.xmlroom.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alonso.xmlroom.data.local.entity.User
import com.alonso.xmlroom.data.preferences.UserPreferences
import com.alonso.xmlroom.data.repository.UserRepository
import com.alonso.xmlroom.databinding.ActivityRegisterBinding
import com.alonso.xmlroom.ui.events.RegisterEvent
import com.alonso.xmlroom.ui.viewmodels.LoginViewModel
import com.alonso.xmlroom.ui.viewmodels.LoginViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(UserPreferences(this), UserRepository())
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInitialData()
        setupObservers()
        setupListeners()
        setupRealtimeValidation()
        setupAnimations()
    }

    private fun setupInitialData() {
        val email = intent.getStringExtra("email")
        if (!email.isNullOrEmpty()) {
            binding.etEmail.setText(email)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is RegisterEvent.Success -> {
                                showSuccessSnackBar(event.message)
                            }

                            is RegisterEvent.Error -> {
                                showErrorSnackBar(event.message)
                            }

                            is RegisterEvent.NavigateToHome -> {
                                navigateToHome()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener { handleRegisterClick() }
        setupRealtimeValidation()
    }

    private fun validateForm(): Boolean {
        // Limpiar errores previos
        binding.tilName.error = null
        binding.tilLastname.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        var isValid = true

        if (binding.etName.text.isNullOrBlank()) {
            binding.tilName.error = "El nombre es obligatorio"
            isValid = false
        }

        if (binding.etLastname.text.isNullOrBlank()) {
            binding.tilLastname.error = "El apellido es obligatorio"
            isValid = false
        }

        val email = binding.etEmail.text.toString().trim()
        if (email.isBlank()) {
            binding.tilEmail.error = "El email es obligatorio"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email inválido"
            isValid = false
        }

        val password = binding.etPassword.text.toString()
        if (password.isBlank()) {
            binding.tilPassword.error = "La contraseña es obligatoria"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Mínimo 6 caracteres"
            isValid = false
        }

        val confirmPassword = binding.etConfirmPassword.text.toString()
        if (confirmPassword.isBlank()) {
            binding.tilConfirmPassword.error = "Confirma la contraseña"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
            isValid = false
        }

        return isValid
    }


    private fun handleRegisterClick() {
        if (validateForm()) {
            val user = User(
                firstName = binding.etName.text.toString().trim(),
                lastName = binding.etLastname.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                password = binding.etPassword.text.toString()
            )
            viewModel.register(user)
        } else {
            scrollToFirstError()
        }
    }

    private fun setupRealtimeValidation() {
        // Email
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.tilEmail.error = "Email inválido"
                } else {
                    binding.tilEmail.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Confirmar contraseña
        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = binding.etPassword.text.toString()
                val confirm = s.toString()

                if (confirm.isNotBlank() && password != confirm) {
                    binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
                } else {
                    binding.tilConfirmPassword.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun showSuccessSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToHome() {
        val intent = Intent(this@RegisterActivity, InsectsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun scrollToFirstError() {
        when {
            binding.tilName.error != null -> binding.tilName.requestFocus()
            binding.tilLastname.error != null -> binding.tilLastname.requestFocus()
            binding.tilEmail.error != null -> binding.tilEmail.requestFocus()
            binding.tilPassword.error != null -> binding.tilPassword.requestFocus()
            binding.tilConfirmPassword.error != null -> binding.tilConfirmPassword.requestFocus()
        }
    }

    private fun setupAnimations() {
        binding.main.alpha = 0f
        binding.main.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
}