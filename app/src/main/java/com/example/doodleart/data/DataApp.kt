package com.example.doodleart.data

import com.example.doodleart.R
import com.example.doodleart.model.ColorModel
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
            ColoringModel(8, R.drawable.img_coloring_9),
            ColoringModel(9, R.drawable.img_coloring_10),
            ColoringModel(10, R.drawable.img_coloring_11),
            ColoringModel(11, R.drawable.img_coloring_12),
            ColoringModel(12, R.drawable.img_coloring_13),
            ColoringModel(13, R.drawable.img_coloring_14),
            ColoringModel(14, R.drawable.img_coloring_15),
            ColoringModel(15, R.drawable.img_coloring_16),
            ColoringModel(16, R.drawable.img_coloring_17),
        )
    }

    fun getListColor() : List<ColorModel>{
        return listOf(
            ColorModel(0, "#FFFFFF" , R.drawable.img_color_add, true),
            ColorModel(1, "#DA2525", R.drawable.img_color_red, true),
            ColorModel(2, "#CC2272", R.drawable.img_color_pink, true),
            ColorModel(3, "#660694", R.drawable.img_color_purple, true),
            ColorModel(4, "#8F04B0", R.drawable.img_color_purple_2, true),
            ColorModel(5, "#023C91", R.drawable.img_color_blue_2, true),
            ColorModel(6, "#0190D6", R.drawable.img_color_blue, true),
            ColorModel(7, "#FFFFFF" , R.drawable.img_color_add, false),
            ColorModel(8, "#DA2525", R.drawable.img_color_bling_red, false),
            ColorModel(9, "#CC2272", R.drawable.img_color_bling_pink, false),
            ColorModel(10, "#660694", R.drawable.img_color_bling_purple, false),
            ColorModel(11, "#8F04B0", R.drawable.img_color_bling_purple_2, false),
            ColorModel(12, "#023C91", R.drawable.img_color_bling_blue_2, false),
            ColorModel(13, "#0190D6", R.drawable.img_color_bling_blue, false),
        )
    }
}