package com.alonso.xmlroom.ui.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alonso.xmlroom.R
import com.alonso.xmlroom.databinding.ActivityDetailsBinding
import com.alonso.xmlroom.data.local.entity.Insect
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailsBinding.inflate(layoutInflater)
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
                .placeholder(android.R.drawable.ic_menu_gallery) // Imagen mientras carga
                .error(android.R.drawable.stat_notify_error)    // Imagen si falla
                .into(binding.ivInsect)
        }
    }
}