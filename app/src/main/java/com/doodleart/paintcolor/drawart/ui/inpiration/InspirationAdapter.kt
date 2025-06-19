package com.doodleart.paintcolor.drawart.ui.inpiration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doodleart.paintcolor.drawart.databinding.ItemColoringBinding
import com.doodleart.paintcolor.drawart.databinding.ItemInpirationBinding
import com.doodleart.paintcolor.drawart.model.ColoringModel
import com.doodleart.paintcolor.drawart.model.InpirationModel
import com.doodleart.paintcolor.drawart.view.base.BaseAdapter

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
