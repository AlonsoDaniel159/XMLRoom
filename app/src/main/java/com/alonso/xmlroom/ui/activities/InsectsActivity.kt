package com.alonso.xmlroom.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.alonso.xmlroom.R
import com.alonso.xmlroom.databinding.ActivityInsectsBinding
import com.alonso.xmlroom.data.local.entity.Insect
import com.alonso.xmlroom.ui.viewmodels.InsectViewModel
import com.alonso.xmlroom.ui.viewmodels.InsectViewModelFactory
import com.alonso.xmlroom.data.preferences.UserPreferences
import com.alonso.xmlroom.data.repository.InsectRepository
import com.alonso.xmlroom.ui.adapters.InsectPagerAdapter
import kotlinx.coroutines.launch

/**
 * MainActivity SIN ViewModel
 * Maneja directamente la base de datos
 */
class InsectsActivity : AppCompatActivity(), InsectActions {

    private lateinit var binding: ActivityInsectsBinding

    private lateinit var viewModel: InsectViewModel

    private val pagerAdapter by lazy { InsectPagerAdapter(this) } // <-- NUEVA PROPIEDAD

    private var currentUserId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsectsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        setupUI()
        initializeUser()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
    }


    private fun initializeUser() {
        lifecycleScope.launch {
            currentUserId = UserPreferences(this@InsectsActivity).getUserId() ?: -1
            val factory = InsectViewModelFactory(InsectRepository(), currentUserId)
            viewModel = ViewModelProvider(this@InsectsActivity, factory)[InsectViewModel::class.java]

            if (currentUserId == -1L) {
                redirectToLogin()
                return@launch
            }

            setupViewPager()
            setupObservers()
            setupListeners()
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = pagerAdapter
    }

    /**
     * OBSERVADORES - Escuchan cambios en el ViewModel
     */
    private fun setupObservers() {
        // Observar lista de insectos
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    // Si estamos en la página 0, activamos el filtro "ALL" y marcamos el botón
                    viewModel.setFilter(InsectViewModel.FilterType.ALL)
                    binding.toggleGroup.check(R.id.btnAllInsects)
                } else {
                    // Si estamos en la página 1, activamos el filtro "USER" y marcamos el botón
                    viewModel.setFilter(InsectViewModel.FilterType.USER)
                    binding.toggleGroup.check(R.id.btnMyInsects)
                }
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                    showLogoutDialog()
                    true
                }
                else -> false
            }
        }

        // Ahora los botones no llaman al ViewModel directamente, sino que cambian la página del ViewPager.
        // El ViewPager, al cambiar de página, llamará a setFilter gracias al observer que acabamos de crear.
        binding.btnAllInsects.setOnClickListener {
            binding.viewPager.currentItem = 0
        }

        binding.btnMyInsects.setOnClickListener {
            binding.viewPager.currentItem = 1
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
                    Log.i("userid", currentUserId.toString())
                    viewModel.addInsect(name, url, currentUserId)
                } else {
                    Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onInsectLongPressed(insect: Insect) {
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

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  //
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
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        lifecycleScope.launch {
            // Limpiar sesión
            UserPreferences(this@InsectsActivity).clearSession()
            // Navegar al Login
            redirectToLogin()
        }
    }
}