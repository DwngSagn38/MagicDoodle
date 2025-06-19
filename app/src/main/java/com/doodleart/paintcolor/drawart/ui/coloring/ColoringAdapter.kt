package com.example.doodleart.ui.coloring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doodleart.databinding.ItemColoringBinding
import com.example.doodleart.model.ColoringModel
import com.example.doodleart.view.base.BaseAdapter

class ColoringAdapter(
    private val onClick: (ColoringModel) -> Unit
) : BaseAdapter<ItemColoringBinding,ColoringModel>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int,
    ): ItemColoringBinding {
        return ItemColoringBinding.inflate(inflater, parent, false)
    }

    override fun creatVH(binding: ItemColoringBinding): RecyclerView.ViewHolder {
        return ColoringViewHolder(binding)
    }

    inner class ColoringViewHolder(binding: ItemColoringBinding) : BaseVH<ColoringModel>(binding) {
        override fun bind(data: ColoringModel) {
            binding.apply {
                imgColoring.setImageResource(data.img)
                root.setOnClickListener {
                    onClick.invoke(data)
                }
            }
        }

    }
}