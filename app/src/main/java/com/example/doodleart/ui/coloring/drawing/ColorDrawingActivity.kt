package com.example.doodleart.ui.coloring.drawing

import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.SeekBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.data.DataApp
import com.example.doodleart.databinding.ActivityColorDrawingBinding
import com.example.doodleart.model.ColorModel
import com.example.doodleart.utils.showColorPicker
import com.example.doodleart.widget.gone
import com.example.doodleart.widget.tap
import com.example.doodleart.widget.visible

class ColorDrawingActivity : BaseActivity<ActivityColorDrawingBinding>() {
    private var idColoring : Int = 0
    private lateinit var adapterColor : ColorDrawingAdapter
    private lateinit var currentColor : String
    private var currentColorInt: Int = Color.WHITE
    private var brushSizePx: Int = 10
    private lateinit var listColor : List<ColorModel>
    private var isPreview = false

    override fun setViewBinding(): ActivityColorDrawingBinding {
        return ActivityColorDrawingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        idColoring = intent.getIntExtra("id", 0)
        val bitmap = BitmapFactory.decodeResource(resources, DataApp.getListColoring()[idColoring].img)
        binding.zoomablePaintView.loadImage(bitmap)
        binding.imgBack.tap { finish() }
        binding.zoomablePaintView.setBrushColor(currentColorInt)
        binding.imgFloodFill.setColorFilter(R.drawable.gradient_tint)
        binding.zoomablePaintView.setFloodFillMode(true)
        setDataColor(true)
    }

    override fun viewListener() {
        binding.seekBarBrush.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                val progressRatio = value / sb!!.max.toFloat()
                binding.trapezoidProgress.progress = progressRatio
                brushSizePx = value + 1
                binding.zoomablePaintView.setBrushSize(brushSizePx)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        binding.apply {
            imgFloodFill.tap {
                chooseTypeDraw(true)
            }
            imgBrush.tap {
                chooseTypeDraw(false)
            }
            imgUndo.setOnClickListener {
                zoomablePaintView.undo()
                updateUndoRedoUI()
            }
            imgRedo.setOnClickListener {
                zoomablePaintView.redo()
                updateUndoRedoUI()
            }
            imgPreview.tap {
                isPreview = !isPreview
                zoomablePaintView.setPreviewMode(isPreview)
                Toast.makeText(this@ColorDrawingActivity, "Preview: $isPreview", Toast.LENGTH_SHORT).show()
            }
            imgColorType.tap {
                llColorType.visible()
            }

            imgColorDefault.tap {
                setDataColor(true)
                adapterColor.setCheck(0)
                imgColorType.setImageResource(R.drawable.img_color_default)
                zoomablePaintView.setBlingMode(false)
                llColorType.gone()
            }
            imgColorBling.tap {
                setDataColor(false)
                adapterColor.setCheck(7)
                zoomablePaintView.setBlingMode(true)
                imgColorType.setImageResource(R.drawable.img_color_bling)
                llColorType.gone()
            }
        }

        binding.zoomablePaintView.onUndoRedoCountChanged = { undo, redo ->
            updateUndoRedoUI()
        }

    }

    override fun dataObservable() {
    }

    private fun chooseTypeDraw(type : Boolean){
        binding.zoomablePaintView.setFloodFillMode(type)
        if (type){
            binding.imgFloodFill.setColorFilter(R.drawable.gradient_tint)
            binding.imgBrush.setColorFilter(0)
        }else{
            binding.imgBrush.setColorFilter(R.drawable.gradient_tint)
            binding.imgFloodFill.setColorFilter(0)
        }
    }

    private fun setDataColor(type : Boolean){
        adapterColor = ColorDrawingAdapter {
            currentColor = it.color
            adapterColor.setCheck(it.id)
            if (it.id == 0 || it.id == 7){
                dialogPickColor()
            }
            currentColorInt = Color.parseColor(currentColor)
            binding.zoomablePaintView.setBrushColor(currentColorInt)
        }

        binding.rcvColoring.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcvColoring.adapter = adapterColor

        listColor = DataApp.getListColor().filter { it.type == type }
        adapterColor.addList(listColor.toMutableList())

        val defaultColor = DataApp.getListColor().firstOrNull()
        defaultColor?.let {
            currentColor = it.color
            currentColorInt = Color.parseColor(currentColor)
            binding.zoomablePaintView.setBrushColor(currentColorInt)

            // Nếu muốn đánh dấu chọn item đầu tiên trong adapter:
            adapterColor.setCheck(0)
        }
    }

    private fun dialogPickColor(){
        showColorPicker(currentColorInt,
            onColorPicked = { colorString ->
                currentColorInt = Color.parseColor(colorString)
                binding.zoomablePaintView.setBrushColor(currentColorInt)
            },
            onDismiss = { }
        )
    }

    private fun updateUndoRedoUI() {
        val undoCount = binding.zoomablePaintView.undoCount
        val redoCount = binding.zoomablePaintView.redoCount

        binding.imgUndo.setColorFilter(if (undoCount > 1) Color.WHITE else Color.GRAY)
        binding.imgRedo.setColorFilter(if (redoCount > 0) Color.WHITE else Color.GRAY)
    }

}