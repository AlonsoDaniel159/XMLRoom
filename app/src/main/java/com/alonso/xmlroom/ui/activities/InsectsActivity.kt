package com.alonso.xmlroom.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alonso.xmlroom.R
import com.alonso.xmlroom.databinding.ActivityInsectsBinding
import com.alonso.xmlroom.data.local.entity.Insect
import com.alonso.xmlroom.ui.adapters.InsectAdapter
import com.alonso.xmlroom.ui.viewmodels.InsectViewModel
import com.alonso.xmlroom.ui.viewmodels.InsectViewModelFactory
import com.alonso.xmlroom.utils.UiState
import com.alonso.xmlroom.data.preferences.UserPreferences
import com.alonso.xmlroom.data.repository.InsectRepository
import kotlinx.coroutines.launch

/**
 * MainActivity SIN ViewModel
 * Maneja directamente la base de datos
 */
class InsectsActivity : AppCompatActivity(), InsectActions {

    private lateinit var binding: ActivityInsectsBinding

    private val viewModel: InsectViewModel by viewModels { InsectViewModelFactory(InsectRepository())}

    private val adapter by lazy { InsectAdapter(this) }

    private var currentUserId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsectsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        initializeUser()
    }


    private fun initializeUser() {
        lifecycleScope.launch {
            currentUserId = UserPreferences(this@InsectsActivity).getUserId() ?: -1

            if (currentUserId == -1L) {
                redirectToLogin()
                return@launch
            }

            viewModel.loadInsectsForUser(currentUserId)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            val staggeredGridLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            staggeredGridLayoutManager.gapStrategy =
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

            layoutManager = staggeredGridLayoutManager
            adapter = this@InsectsActivity.adapter
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
                        Toast.makeText(this@InsectsActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            showAddDialog()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_import_data -> {
                    // Lógica para importar datos
                    Toast.makeText(this, "Importando datos...", Toast.LENGTH_SHORT).show()
                    true // Indica que el evento ha sido manejado
                }
                R.id.action_logout -> {
                    // Lógica para cerrar sesión
                    // viewModel.logout()
                    Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        binding.btnAllInsects.setOnClickListener {
            if (! binding.btnAllInsects.isChecked) {
                binding.btnAllInsects.isChecked = true
                return@setOnClickListener
            }

            // Deseleccionar el otro botón
            binding.btnMyInsects.isChecked = false

            // Mostrar todos los insectos
            viewModel.getAllInsects()
        }

        // ✅ Botón "Mis insectos"
        binding.btnMyInsects.setOnClickListener {
            if (!binding.btnMyInsects.isChecked) {
                binding.btnMyInsects.isChecked = true
                return@setOnClickListener
            }

            // Deseleccionar el otro botón
            binding.btnAllInsects.isChecked = false

            // Filtrar por usuario
            viewModel.getInsectsByUser()
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
                    viewModel.addInsect(name, url, currentUserId)
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
        val intent = Intent(this, DetailsActivity::class.java)
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
        binding.progressBar.visibility = View.VISIBLE
    }

    /**
     * Mostrar datos en el RecyclerView
     */
    private fun showData(insects: List<Insect>) {
        binding.progressBar.visibility = View.GONE

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

    private fun redirectToLogin() {
        Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // ✅ Agregar
        startActivity(intent)
        finish()
    }


    //===========MENU===========
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R. menu.menu_insects, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // Cambiar color del "Cerrar sesión" a rojo
        val logoutItem = menu.findItem(R.id.action_logout)
        val spannable = SpannableString(logoutItem.title)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, android.R. color.holo_red_dark)),
            0,
            spannable.length,
            0
        )
        logoutItem. title = spannable

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_settings -> {
                Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_import_data -> {
                Toast.makeText(this, "Importando data", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_logout -> {
                Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}