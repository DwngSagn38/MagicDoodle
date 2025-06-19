package com.example.doodleart.ui.free_creation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.doodleart.R
import com.example.doodleart.data.brushPainTing

class BrushAdapter(
    private val items: List<brushPainTing>,
    private val onBrushSelected: (brushPainTing) -> Unit
) : RecyclerView.Adapter<BrushAdapter.BrushViewHolder>() {

    inner class BrushViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivLine: ImageView = view.findViewById(R.id.iv_Line)
        private val ivSelected: ImageView = view.findViewById(R.id.tv_isSelected)
        private val cardView: CardView = view.findViewById(R.id.cardViewExample)

        fun bind(item: brushPainTing, position: Int) {
            item.img?.let { ivLine.setBackgroundResource(it) }
            ivSelected.visibility = if (item.isSelected) View.VISIBLE else View.GONE

            cardView.setOnClickListener {
                onBrushSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrushViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_brush, parent, false)
        return BrushViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrushViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
