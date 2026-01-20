// en: app/src/main/java/com/alonso/xmlroom/ui/adapters/InsectPagerAdapter.kt

package com.alonso.xmlroom.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alonso.xmlroom.ui.fragments.InsectListFragment
import com.alonso.xmlroom.ui.viewmodels.InsectViewModel
import com.alonso.xmlroom.ui.viewmodels.InsectViewModel.FilterType

class InsectPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {

    // Tenemos dos pestañas: "Todos" (posición 0) y "Mis Insectos" (posición 1)
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InsectListFragment.newInstance(FilterType.ALL)
            1 -> InsectListFragment.newInstance(FilterType.USER)
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
