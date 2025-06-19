package com.doodleart.paintcolor.drawart.model

data class ColorModel(val id : Int, val color : String, val img : Int, var type : Boolean){
    var active: Boolean = false
}
