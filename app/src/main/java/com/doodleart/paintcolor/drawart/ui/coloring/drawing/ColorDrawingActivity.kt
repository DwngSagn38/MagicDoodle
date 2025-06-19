package com.doodleart.paintcolor.drawart.ui.coloring.drawing

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.doodleart.paintcolor.drawart.R
import com.doodleart.paintcolor.drawart.base.BaseActivity
import com.doodleart.paintcolor.drawart.data.DataApp
import com.doodleart.paintcolor.drawart.databinding.ActivityColorDrawingBinding
import com.doodleart.paintcolor.drawart.dialog.DeleteDialog
import com.doodleart.paintcolor.drawart.model.ColorModel
import com.doodleart.paintcolor.drawart.model.MyFileModel
import com.doodleart.paintcolor.drawart.roomdb.DBHelper
import com.doodleart.paintcolor.drawart.ui.custom_view.ZoomablePaintView
import com.doodleart.paintcolor.drawart.ui.my_file.MyFileActivity
import com.doodleart.paintcolor.drawart.ui.my_file.MyFileDetailActivity
import com.doodleart.paintcolor.drawart.ui.my_file.fragment.MyFileAdapter
import com.doodleart.paintcolor.drawart.utils.showColorPicker
import com.doodleart.paintcolor.drawart.widget.gone
import com.doodleart.paintcolor.drawart.widget.invisible
import com.doodleart.paintcolor.drawart.widget.savePaintViewToFile
import com.doodleart.paintcolor.drawart.widget.setGradientText
import com.doodleart.paintcolor.drawart.widget.tap
import com.doodleart.paintcolor.drawart.widget.visible
import kotlinx.coroutines.launch

class ColorDrawingActivity : BaseActivity<ActivityColorDrawingBinding>() {
    private var idColoring : Int = 0
    private lateinit var myFile : MyFileModel
    private var isEdit : Boolean = false
    private var isModeType : Boolean = false
    private lateinit var adapterColor : ColorDrawingAdapter
    private lateinit var currentColor : String
    private var currentColorInt: Int = Color.YELLOW
    private var brushSizePx: Int = 20
    private lateinit var listColor : List<ColorModel>
    private var isPreview = false
    private lateinit var loadingDialog: Dialog
    private lateinit var dbHelper: DBHelper

    override fun setViewBinding(): ActivityColorDrawingBinding {
        return ActivityColorDrawingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        dbHelper = DBHelper.getDatabase(this)
        isEdit = intent.getBooleanExtra("edit", false)
        idColoring = intent.getIntExtra("id", 0)
        setData(isEdit)

        initLoadingDialog()
        binding.llProgress.bringToFront()
        binding.imgBack.tap { showDialogConfirm() }
        binding.zoomablePaintView.setBrushColor(currentColorInt)
        binding.zoomablePaintView.setFloodFillMode(true)
        setDataColor(true)
        onBackPressedDispatcher.addCallback(this) {
            showDialogConfirm()
        }
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
            imgLockColoringOn.tap {
                lockColoring()
                imgLockColoringOn.invisible()
                imgLockColoringOff.visible()
            }
            imgLockColoringOff.tap {
                lockColoring()
                imgLockColoringOn.visible()
                imgLockColoringOff.invisible()
            }
            imgColorType.setOnClickListener {
                isModeType = !isModeType
                if (isModeType){
                    llColorType.visible()
                }else{
                    llColorType.gone()
                }

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
//            imgRePlay.tap {
//                zoomablePaintView.replayFromUndoStack(300L)
//            }
            imgSave.tap { showDialogSave() }
        }

        binding.zoomablePaintView.onUndoRedoCountChanged = { undo, redo ->
            updateUndoRedoUI()
        }
        binding.zoomablePaintView.setFloodFillListener(object :
            ZoomablePaintView.FloodFillListener {
            override fun onFloodFillStart() {
                showLoading(true)
            }

            override fun onFloodFillDone() {
                showLoading(false)
            }
        })


    }

    private fun lockColoring(){
        isPreview = !isPreview
        binding.zoomablePaintView.setPreviewMode(isPreview)
        val mess = if (isPreview) getString(R.string.preview_on) else getString(R.string.preview_off)
        Toast.makeText(this@ColorDrawingActivity, mess, Toast.LENGTH_SHORT).show()
    }
    override fun dataObservable() {
    }

    private fun chooseTypeDraw(type : Boolean){
        binding.zoomablePaintView.setFloodFillMode(type)
        if (type){
            binding.imgFloodFill.setBackgroundResource(R.drawable.bg_color_type_select)
            binding.imgBrush.setBackgroundResource(R.drawable.bg_color_type)
        }else{
            binding.imgBrush.setBackgroundResource(R.drawable.bg_color_type_select)
            binding.imgFloodFill.setBackgroundResource(R.drawable.bg_color_type)
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
            binding.llColorType.gone()
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


    private fun initLoadingDialog() {
        loadingDialog = Dialog(this).apply {
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Ẩn thanh điều hướng
            window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )

            val tvApp = findViewById<TextView>(R.id.tvApp)
            tvApp.setGradientText(context)
        }
    }



    private fun showLoading(show: Boolean) {
        if (show) {
            if (!loadingDialog.isShowing) loadingDialog.show()
        } else {
            if (loadingDialog.isShowing) loadingDialog.dismiss()
        }
    }

    private fun showDialogSave(){
        val mess = getString(R.string.are_you_save_it)
        val title = getString(R.string.save_it)
        val dialog = DeleteDialog(this, title,mess,
            action = {
                binding.zoomablePaintView.resetZoomAndPan()
                lifecycleScope.launch {
                    val path = savePaintViewToFile(binding.zoomablePaintView, this@ColorDrawingActivity)
                    if (isEdit){
                        dbHelper.fileDao().updateFile(MyFileModel(id = myFile.id , path = path, type = false, createdAt = System.currentTimeMillis()))
                    }else{
                        dbHelper.fileDao().insertFile(MyFileModel(path = path, type = false))
                    }
                }
                showActivity(MyFileActivity::class.java)
                finishAffinity()
            },
            no = {})
        dialog.show()
    }

    private fun showDialogConfirm() {
//        val mess = getString(R.string.are_you_want_save_it)
        val dialog = DeleteDialog(
            this,
            action = {
                binding.zoomablePaintView.resetZoomAndPan()
                lifecycleScope.launch {
                    val path = savePaintViewToFile(binding.zoomablePaintView, this@ColorDrawingActivity)
                    if (isEdit){
                        dbHelper.fileDao().updateFile(MyFileModel(id = myFile.id , path = path, type = false, createdAt = System.currentTimeMillis()))
                    }else{
                        dbHelper.fileDao().insertFile(MyFileModel(path = path, type = false))
                    }
                    finish()
                }
            },
            no = {
                finish() // Chọn No thì gọi finish() luôn
            }
        )
        dialog.show()
    }

    private fun setData(isEdit: Boolean) {
        Log.d("ColorDrawing" , "is edit $isEdit")
        if (isEdit) {
            lifecycleScope.launch {
                myFile = dbHelper.fileDao().getFileById(idColoring)!!
                val bitmap = BitmapFactory.decodeFile(myFile.path)
                binding.zoomablePaintView.loadImage(bitmap,true)
            }
        } else {
            val bitmap = BitmapFactory.decodeResource(resources, DataApp.getListColoring()[idColoring].img)
            binding.zoomablePaintView.loadImage(bitmap,false)
        }
    }
}