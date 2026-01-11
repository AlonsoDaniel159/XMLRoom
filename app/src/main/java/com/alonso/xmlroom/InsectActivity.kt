package com.alonso.xmlroom

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alonso.xmlroom.databinding.ActivityInsectBinding
import com.alonso.xmlroom.room.entity.Insect
import com.bumptech.glide.Glide

class InsectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInsectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadInsect()
    }

    private fun loadInsect() {
        val insect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_INSECT", Insect::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Insect>("EXTRA_INSECT")
        }

        // Si el insecto no es nulo, rellenamos la UI
        insect?.let {
            binding.tvName.text = it.name

            Glide.with(this)
                .load(it.imgLocation)
                .into(binding.ivInsect)
        }
    }
}