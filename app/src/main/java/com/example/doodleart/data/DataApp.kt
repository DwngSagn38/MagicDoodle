package com.example.doodleart.data

import com.example.doodleart.R
import com.example.doodleart.model.ColoringModel

object DataApp {
    fun getListColoring() : List<ColoringModel>{
        return listOf(
            ColoringModel(0, R.drawable.img_coloring_1),
            ColoringModel(1, R.drawable.img_coloring_2),
            ColoringModel(2, R.drawable.img_coloring_3),
            ColoringModel(3, R.drawable.img_coloring_4),
            ColoringModel(4, R.drawable.img_coloring_5),
            ColoringModel(5, R.drawable.img_coloring_6),
            ColoringModel(6, R.drawable.img_coloring_7),
            ColoringModel(7, R.drawable.img_coloring_8),
        )
    }
}