package com.example.doodleart.ui.inpiration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doodleart.databinding.ItemColoringBinding
import com.example.doodleart.databinding.ItemInpirationBinding
import com.example.doodleart.model.ColoringModel
import com.example.doodleart.model.InpirationModel
import com.example.doodleart.view.base.BaseAdapter

class InspirationAdapter(
        private val onClick: (InpirationModel) -> Unit
    ) : BaseAdapter<ItemInpirationBinding, InpirationModel>() {
        override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int,
        ): ItemInpirationBinding {
            return ItemInpirationBinding.inflate(inflater, parent, false)
        }

        override fun creatVH(binding: ItemInpirationBinding): RecyclerView.ViewHolder {
            return ColoringViewHolder(binding)
        }

        inner class ColoringViewHolder(binding: ItemInpirationBinding) : BaseVH<InpirationModel>(binding) {
            override fun bind(data: InpirationModel) {
                binding.apply {
                    imgColoring.setImageResource(data.img)
                    root.setOnClickListener {
                        onClick.invoke(data)
                    }
                }
            }

        }
    }
