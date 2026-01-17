package com.alonso.xmlroom.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.alonso.xmlroom.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    override fun onStart() {
        super.onStart()
        val email = intent.getStringExtra("email")
        binding.etEmail.setText(email)
    }

    private fun initListeners() {

    }
}