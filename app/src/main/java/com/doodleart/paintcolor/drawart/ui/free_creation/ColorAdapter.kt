package com.doodleart.paintcolor.drawart.ui.free_creation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doodleart.paintcolor.drawart.R
class ColorAdapter(
    private val colors: Array<Int>,
    private val onItemClick: (Int) -> Unit,
    selectedColor: Int // 👈 màu được chọn ban đầu
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private var selectedIndex = colors.indexOfFirst { it == selectedColor } // ✅ tự tìm index theo màu

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewColor: View = itemView.findViewById(R.id.viewColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = 30f
        }

        holder.viewColor.background = drawable

        val tvIsSelected = holder.itemView.findViewById<View>(R.id.tv_isSelected)
        tvIsSelected.visibility = if (position == selectedIndex) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val adapterPos = holder.adapterPosition
            if (adapterPos == RecyclerView.NO_POSITION) return@setOnClickListener

            val previousIndex = selectedIndex
            selectedIndex = adapterPos
            notifyItemChanged(previousIndex)
            notifyItemChanged(selectedIndex)
            onItemClick(adapterPos)
        }
    }

    override fun getItemCount(): Int = colors.size
}
