package com.example.doodleart.ui.my_file.fragment

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.doodleart.R
import com.example.doodleart.model.MyFileModel

class MyFileAdapter(
    private val list: List<MyFileModel>,
    private val onItemClick: (MyFileModel) -> Unit
) : RecyclerView.Adapter<MyFileAdapter.MyFileViewHolder>() {

    inner class MyFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imgSaved)

        fun bind(item: MyFileModel) {
            val bitmap = BitmapFactory.decodeFile(item.path)
            imageView.setImageBitmap(bitmap)

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_file, parent, false)
        return MyFileViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFileViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}
