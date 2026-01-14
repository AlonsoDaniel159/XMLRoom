package com.alonso.xmlroom

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alonso.xmlroom.databinding.ActivityMainBinding
import com.alonso.xmlroom.room.RoomApp
import com.alonso.xmlroom.room.entity.Insect
import com.alonso.xmlroom.room.entity.UserAuth
import com.alonso.xmlroom.ui.InsectAdapter
import com.alonso.xmlroom.ui.viewmodels.InsectViewModel
import com.alonso.xmlroom.utils.UiState
import kotlinx.coroutines.launch

/**
 * MainActivity SIN ViewModel
 * Maneja directamente la base de datos
 */
class MainActivity : AppCompatActivity(), InsectActions {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: InsectViewModel by viewModels()

    private val adapter by lazy { InsectAdapter(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Simular usuario autenticado
        RoomApp.auth = UserAuth(1, "Usuario Demo")

        setupRecyclerView()
        setupObservers()  // 👈 CLAVE: Observar los LiveData
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            val staggeredGridLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            staggeredGridLayoutManager.gapStrategy =
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

            layoutManager = staggeredGridLayoutManager
            adapter = this@MainActivity.adapter
        }
    }

    /**
     * 👁️ OBSERVADORES - Escuchan cambios en el ViewModel
     */
    private fun setupObservers() {
        // Observar lista de insectos

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observar estado de la lista de insectos
                launch {
                    viewModel.insects.collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showLoading()
                            is UiState.Success -> showData(uiState.data)
                            is UiState.Error -> showError(uiState.message)
                        }
                    }
                }

                // Observar mensajes (toasts)
                launch {
                    viewModel.message.collect { message ->
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        // Creamos un contenedor lineal para los dos EditText
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val padding = 50
            setPadding(padding, 20, padding, 10)
        }

        val inputName = EditText(this).apply {
            hint = "Nombre del insecto"
            setSingleLine()
        }

        val inputUrl = EditText(this).apply {
            hint = "URL de la imagen (Glide)"
            setSingleLine()
        }

        container.addView(inputName)
        container.addView(inputUrl)

        AlertDialog.Builder(this)
            .setTitle("Nuevo Insecto")
            .setMessage("Ingresa los datos del insecto")
            .setView(container) // Seteamos el contenedor que tiene ambos
            .setPositiveButton("Agregar") { _, _ ->
                val name = inputName.text.toString()
                val url = inputUrl.text.toString()

                if (name.isNotBlank()) {
                    viewModel.addInsect(name, url)
                } else {
                    Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onInsectLongPressed(insect: Insect) {
        // Aquí llamas al diálogo de confirmación que ya tienes
        showDeleteConfirmation(insect)
    }

    override fun onInsectClicked(insect: Insect) {
        val intent = Intent(this, InsectActivity::class.java)
        intent.putExtra("EXTRA_INSECT", insect)
        startActivity(intent)
    }

    private fun showDeleteConfirmation(insect: Insect) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("¿Eliminar ${insect.name}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteInsect(insect)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Mostrar indicador de carga
     */
    private fun showLoading() {
        binding.recyclerView.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        // Si tienes un ProgressBar, muéstralo aquí:
        // binding.progressBar.visibility = View.VISIBLE
    }

    /**
     * Mostrar datos en el RecyclerView
     */
    private fun showData(insects: List<Insect>) {
        // binding.progressBar.visibility = View.GONE

        if (insects.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            adapter.submitList(insects)
        }
    }

    /**
     * Mostrar error
     */
    private fun showError(message: String) {
        // binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.tvEmpty.visibility = View.VISIBLE
        binding.tvEmpty.text = message  // Mostrar el mensaje de error

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}