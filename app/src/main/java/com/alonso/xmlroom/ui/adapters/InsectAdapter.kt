package com.alonso.xmlroom.ui.adapters

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alonso.xmlroom.data.local.entity.Insect
import com.alonso.xmlroom.databinding.ItemInsectBinding
import com.alonso.xmlroom.ui.activities.InsectActions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class InsectAdapter(private val listener: InsectActions) : ListAdapter<Insect, InsectAdapter.InsectViewHolder>(InsectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsectViewHolder {
        val binding = ItemInsectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InsectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InsectViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    inner class InsectViewHolder(val binding: ItemInsectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(insect: Insect) {
            binding.apply {
                tvName.text = insect.name

                Glide.with(ivInsect.context) // Es mejor usar el contexto de la vista
                    .load(insect.imgLocation)
                    .placeholder(R.drawable.ic_menu_gallery) // Imagen mientras carga
                    .error(R.drawable.stat_notify_error)    // Imagen si falla
                    .diskCacheStrategy(DiskCacheStrategy.ALL)       // Cache inteligente
                    .into(ivInsect)

                itemView.setOnLongClickListener {
                    listener.onInsectLongPressed(insect)
                    true
                }

                itemView.setOnClickListener {
                    listener.onInsectClicked(insect)
                }
            }
        }
    }

    private class InsectDiffCallback() : DiffUtil.ItemCallback<Insect>() {
        override fun areItemsTheSame(oldItem: Insect, newItem: Insect): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Insect, newItem: Insect): Boolean {
            return oldItem == newItem
        }
    }

}