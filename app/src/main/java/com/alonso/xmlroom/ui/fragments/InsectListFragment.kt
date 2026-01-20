// RUTA: app/src/main/java/com/alonso/xmlroom/ui/fragments/InsectListFragment.kt

package com.alonso.xmlroom.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alonso.xmlroom.data.local.entity.Insect
import com.alonso.xmlroom.databinding.FragmentInsectListBinding
import com.alonso.xmlroom.ui.activities.InsectActions
import com.alonso.xmlroom.ui.adapters.InsectAdapter
import com.alonso.xmlroom.ui.viewmodels.InsectViewModel
import com.alonso.xmlroom.utils.UiState
import kotlinx.coroutines.launch
import com.alonso.xmlroom.ui.viewmodels.InsectViewModel.FilterType

class InsectListFragment : Fragment() {

    // 1. Binding para el layout del fragment
    private var _binding: FragmentInsectListBinding? = null

    private lateinit var filterType: FilterType

    private val binding get() = _binding!!

    // 2. ViewModel compartido con la Activity
    private val viewModel: InsectViewModel by activityViewModels()

    // 3. Adapter para el RecyclerView, ahora vive en el Fragment
    private val insectAdapter by lazy {
        // La Activity debe implementar InsectActions para manejar los clics
        InsectAdapter(requireActivity() as InsectActions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Leemos el tipo de filtro que nos pasaron al crear el fragment
        arguments?.let {
            // Usamos el nombre de la enum como String para recuperarlo
            filterType = FilterType.valueOf(it.getString(ARG_FILTER_TYPE) ?: FilterType.ALL.name)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflamos el layout del fragment
        _binding = FragmentInsectListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Una vez la vista está creada, configuramos el RecyclerView y los Observers
        setupRecyclerView()
        setupFilteredObserver()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewFragment.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = insectAdapter
        }
    }

    private fun setupFilteredObserver() {
        val flowToObserve = when (filterType) {
            FilterType.ALL -> viewModel.allInsects
            FilterType.USER -> viewModel.userInsects
        }

        // Observamos el ciclo de vida del fragment para recolectar datos del ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flowToObserve.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> showData(uiState.data)
                        is UiState.Error -> showError(uiState.message)
                    }
                }
            }
        }
    }

    // Funciones para actualizar la UI, ahora encapsuladas en el Fragment
    private fun showLoading() {
        binding.progressBarFragment.isVisible = true
        binding.recyclerViewFragment.isVisible = false
        binding.tvEmptyFragment.isVisible = false
    }

    private fun showData(insects: List<Insect>) {
        binding.progressBarFragment.isVisible = false
        if (insects.isEmpty()) {
            binding.recyclerViewFragment.isVisible = false
            binding.tvEmptyFragment.isVisible = true
        } else {
            binding.recyclerViewFragment.isVisible = true
            binding.tvEmptyFragment.isVisible = false
            insectAdapter.submitList(insects)
        }
    }

    private fun showError(message: String?) {
        binding.progressBarFragment.isVisible = false
        binding.recyclerViewFragment.isVisible = false
        binding.tvEmptyFragment.isVisible = true
        binding.tvEmptyFragment.text = message ?: "Ocurrió un error"
    }

    // NUEVO: Companion object para crear instancias del fragment de forma segura
    companion object {
        private const val ARG_FILTER_TYPE = "filter_type"

        fun newInstance(filterType: FilterType): InsectListFragment {
            val fragment = InsectListFragment()
            val args = Bundle().apply {
                putString(ARG_FILTER_TYPE, filterType.name)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiamos el binding para evitar fugas de memoria
        _binding = null
    }
}
