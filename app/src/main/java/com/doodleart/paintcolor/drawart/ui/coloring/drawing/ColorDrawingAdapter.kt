package com.example.doodleart.ui.coloring.drawing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doodleart.R
import com.example.doodleart.databinding.ItemColor1Binding
import com.example.doodleart.databinding.ItemColorBinding
import com.example.doodleart.model.ColorModel
import com.example.doodleart.view.base.BaseAdapter

class ColorDrawingAdapter(
    private val onClick: (ColorModel) -> Unit
) : BaseAdapter<ItemColor1Binding, ColorModel>() {

    private var selectedPosition = RecyclerView.NO_POSITION


    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int,
    ): ItemColor1Binding {
        return  ItemColor1Binding.inflate(inflater, parent, false)
    }

    override fun creatVH(binding: ItemColor1Binding): RecyclerView.ViewHolder {
        return ColorDrawingVH(binding)
    }

    inner class ColorDrawingVH(binding: ItemColor1Binding) : BaseVH<ColorModel>(binding) {
        override fun bind(data: ColorModel) {
            binding.apply {
                imgColoring.setImageResource(data.img)


                if (data.active) {
                    layoutItem.setBackgroundResource(R.drawable.bg_color_item_select)
                }
                else {
                    layoutItem.setBackgroundResource(R.drawable.bg_color_item)
                }

                root.setOnClickListener {
                    val oldPosition = selectedPosition
                    selectedPosition = adapterPosition

                    // ✅ Cập nhật item cũ và mới
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)

                    onClick.invoke(data)
                }
            }
        }
    }

    fun setCheck(code: Int) {
        for (item in listData) {
            item.active = item.id == code // đặt 'active' cho mục có mã tương ứng
        }
        notifyDataSetChanged()
    }

}