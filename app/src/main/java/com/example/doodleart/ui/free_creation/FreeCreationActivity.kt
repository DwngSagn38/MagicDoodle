package com.example.doodleart.ui.free_creation

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.data.DataApp
import com.example.doodleart.data.Stroke
import com.example.doodleart.data.StrokeShape
import com.example.doodleart.data.brushPainTing
import com.example.doodleart.data.toSerializable
import com.example.doodleart.databinding.ActivityFreeCreationBinding
import com.example.doodleart.testRoom.DrawingDao
import com.example.doodleart.testRoom.DrawingEntity
import com.example.doodleart.ui.custom_view.MandalaDrawView
import com.example.doodleart.utils.showColorPicker
import com.example.doodleart.widget.gone
import com.example.doodleart.widget.tap
import com.example.doodleart.widget.visible
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FreeCreationActivity : BaseActivity<ActivityFreeCreationBinding>() {
    private var currentColorInt: Int = Color.WHITE
    private var checkLLdroinvisible: Boolean = false
    private var checkRcvColor: Boolean = false
    private lateinit var adapter: BrushAdapter
    private var Brush: brushPainTing = brushPainTing(R.drawable.line_2, Color.RED, 0, 0)
    private var isEraserOn: Boolean = false
    private var isRcvPenOn: Boolean = true
    private var idInspiration: Int = 0

    private val brushList = listOf(
        brushPainTing(R.drawable.line_2, Color.RED, 0, 0, isSelected = true),
        brushPainTing(R.drawable.line_15, Color.BLUE, 1, 0),
        brushPainTing(R.drawable.line_3, Color.BLUE, 5, 1),
        brushPainTing(R.drawable.line_5, Color.BLUE, 2, 0),
        brushPainTing(R.drawable.line_6, Color.BLUE, 3, 0),
        brushPainTing(R.drawable.line_7, Color.BLUE, 4, 0),
        brushPainTing(R.drawable.line_8, Color.BLUE, 0, 2),
        brushPainTing(R.drawable.line_9, Color.BLUE, 0, 6),
        brushPainTing(R.drawable.line_10, Color.BLUE, 0, 4),
        brushPainTing(R.drawable.line_11, Color.BLUE, 0, 9),
        brushPainTing(R.drawable.line_12, Color.BLUE, 0, 11),
        brushPainTing(R.drawable.line_13, Color.BLUE, 0, 10),
        brushPainTing(R.drawable.line_14, Color.BLUE, 0, 3),
    )

    override fun setViewBinding(): ActivityFreeCreationBinding {
        return ActivityFreeCreationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        idInspiration = intent.getIntExtra("id", -1)
        if (idInspiration == -1) {
            binding.imgInPiration.gone()
        } else {
            binding.imgInPiration.visible()
            binding.imgInPiration.setImageResource(DataApp.getListInpiration()[idInspiration].img)
        }
        binding.mainContainer.tap {
            goneDilog()
        }

        binding.mandalaView.setBackgroundColorCustom(Color.BLACK)
        binding.bg1.setImageResource(R.drawable.bg_droin_selected)
        Brush = brushPainTing(R.drawable.line_2, Color.RED, 0, 0)
        setBrush(Brush)
        binding.icUndu.alpha = 0.4f
        binding.icRedu.alpha = 0.4f
        binding.strokeWidthSeekBar.max = 50
        binding.strokeWidthSeekBar.progress = 4
        DroinCound()
        setUpColor()
        setUpRcvBrush()
        setUpCardView()

    }

    override fun viewListener() {
//        binding.spacingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                binding.mandalaView.shapeSpacing = progress.toFloat().coerceAtLeast(5f)
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//        })
        binding.strokeWidthSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val width = if (progress < 1) 1f else progress.toFloat()
                binding.mandalaView.setStrokeWidth(width)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.icPen.setOnClickListener {
            goneDilog()
            isRcvPenOn = !isRcvPenOn
            if (isRcvPenOn) {
                binding.clPicBrush.gone()
                binding.icPen.alpha = 1.0f
            } else {
                binding.clPicBrush.visible()
                binding.icPen.alpha = 0.5f
            }
        }
        binding.icSave.tap {
            saveImg()


        }
        binding.icNewFile.setOnClickListener {
            showFoundGhost()
        }
        binding.ic4.setOnClickListener {
            goneDilog()
            if (checkLLdroinvisible) {
                checkLLdroinvisible = false
                binding.llDroin.gone()
                binding.llDroin2.gone()
            } else {
                checkLLdroinvisible = true
                binding.llDroin.visible()
                binding.llDroin2.visible()
            }
        }
        binding.icSelecedBackground.setOnClickListener {
            goneDilog()
            if (checkRcvColor) {
                checkRcvColor = false
                binding.recyclerViewColors.gone()
            } else {
                checkRcvColor = true
                binding.recyclerViewColors.visible()
            }
        }
        binding.icBack.tap {
            finish()
        }
        binding.icEraser.setOnClickListener {
            goneDilog()
            val currentlyOn = binding.mandalaView.isEraserOn()
            binding.mandalaView.setEraser(!currentlyOn)
            if (currentlyOn) {
                isEraserOn = false
                binding.icEraser.alpha = 1.0f
                setBrush(Brush)
            } else {
                isEraserOn = true
                binding.icEraser.alpha = 0.5f
                binding.mandalaView.setStrokeShape(StrokeShape.NORMAL)
                binding.mandalaView.setStrokeEffect(MandalaDrawView.StrokeEffect.NORMAL)
            }
        }

        binding.icColor.setOnClickListener {
            showColorPicker(currentColorInt,
                onColorPicked = { colorString ->
                    currentColorInt = Color.parseColor(colorString)
                    binding.mandalaView.setStrokeColor(currentColorInt)
                },
                onDismiss = { }
            )
        }
        binding.icUndu.setOnClickListener {
            binding.mandalaView.undo()
        }

        binding.icRedu.setOnClickListener {
            if (binding.mandalaView.undoneStrokes.isEmpty()) {
                Toast.makeText(this, "Không có gì để Redo!", Toast.LENGTH_SHORT).show()
            } else {
                binding.mandalaView.redo()
            }
        }

        binding.mandalaView.updateUndoRedoState = { canUndo, canRedo ->
            binding.icUndu.isEnabled = canUndo
            binding.icRedu.isEnabled = canRedo
            binding.icUndu.alpha = if (canUndo) 1f else 0.4f
            binding.icRedu.alpha = if (canRedo) 1f else 0.4f
        }


    }


    private fun setUpCardView() {
        val aspectRatio = intent.getStringExtra("aspect_ratio") ?: "1:1"
        val (widthRatio, heightRatio) = when (aspectRatio) {
            "9:16" -> Pair(9f, 16f)
            "1:1" -> Pair(1f, 1f)
            "4:5" -> Pair(4f, 5f)
            else -> Pair(1f, 1f)
        }
        val displayMetrics = resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val marginPx = (20 * displayMetrics.density).toInt()
        val contentWidth = screenWidthPx - marginPx
        val contentHeight = (contentWidth * heightRatio / widthRatio).toInt()
        val mandalaView = findViewById<View>(R.id.mandalaView)
        val cardView = mandalaView.parent as? CardView
        cardView?.let {
            val layoutParams = it.layoutParams
            layoutParams.width = contentWidth
            layoutParams.height = contentHeight
            it.layoutParams = layoutParams
        }
    }

    private fun setUpRcvBrush() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewBrush)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = BrushAdapter(brushList) { selectedBrush ->
            brushList.forEach { it.isSelected = it == selectedBrush }
            adapter.notifyDataSetChanged()
            Brush = selectedBrush
            if (!isEraserOn) {
                setBrush(Brush)
            }
        }

        recyclerView.adapter = adapter
    }

    private fun setBrush(selectedBrush: brushPainTing) {
        when (selectedBrush.effects) {
            0 -> binding.mandalaView.setStrokeEffect(MandalaDrawView.StrokeEffect.NORMAL)
            1 -> binding.mandalaView.setStrokeEffect(MandalaDrawView.StrokeEffect.GLOW)
            2 -> {
                val colors = intArrayOf(
                    Color.parseColor("#28F07B"),
                    Color.parseColor("#FFEA00"),
                    Color.parseColor("#1640FF"),
                )
                binding.mandalaView.setGradientColors(colors)
                binding.mandalaView.setStrokeEffect(MandalaDrawView.StrokeEffect.GLOW_GRADIENT)
            }

            3 -> {
                val colors = intArrayOf(
                    Color.parseColor("#F0282B"),
                    Color.parseColor("#323FFF"),
                    Color.parseColor("#E1FF01"),
                )
                binding.mandalaView.setGradientColors(colors)
                binding.mandalaView.setStrokeEffect(MandalaDrawView.StrokeEffect.GLOW_GRADIENT)
            }

            4 -> {
                val colors = intArrayOf(
                    Color.parseColor("#F0287F"),
                    Color.parseColor("#E1FF00"),
                    Color.parseColor("#C800FF"),
                )
                binding.mandalaView.setGradientColors(colors)
                binding.mandalaView.setStrokeEffect(MandalaDrawView.StrokeEffect.GLOW_GRADIENT)
            }
        }

        when (selectedBrush.shapes) {
            0 -> binding.mandalaView.setStrokeShape(StrokeShape.NORMAL)
            1 -> binding.mandalaView.setStrokeShape(StrokeShape.CIRCLE)
            2 -> binding.mandalaView.setStrokeShape(StrokeShape.HEART)
            3 -> binding.mandalaView.setStrokeShape(StrokeShape.STAR)
            4 -> binding.mandalaView.setStrokeShape(StrokeShape.SQUARE)
            5 -> binding.mandalaView.setStrokeShape(StrokeShape.TRIANGLE)
            6 -> binding.mandalaView.setStrokeShape(StrokeShape.HEXAGON)
            7 -> binding.mandalaView.setStrokeShape(StrokeShape.SMILEY_FACE)
            8 -> {
                val ande = arrayOf("ic1", "ic2", "ic3", "ic4")
                AlertDialog.Builder(this)
                    .setTitle("Chọn hình dạng nét vẽ")
                    .setItems(ande) { _, which ->
                        when (which) {
                            2 -> binding.mandalaView.setImgPen(R.drawable.icon1)
                        }
                    }.show()
                binding.mandalaView.setStrokeShape(StrokeShape.IMAGE)
            }

            9 -> binding.mandalaView.setStrokeShape(StrokeShape.STAR4)
            10 -> binding.mandalaView.setStrokeShape(StrokeShape.PLUS)
            11 -> binding.mandalaView.setStrokeShape(StrokeShape.ELLIPSE)
            12 -> binding.mandalaView.setStrokeShape(StrokeShape.DASHED)
        }
        selectedBrush.color1?.let { binding.mandalaView.setStrokeColor(it) }
    }


    private fun setUpColor() {
        val colors = arrayOf(
            Color.BLACK,
            Color.WHITE,
            Color.YELLOW,
            Color.BLUE,
            Color.MAGENTA,
            Color.parseColor("#4167D7"),
            Color.parseColor("#41D782"),
            Color.parseColor("#EA575D"),
            Color.parseColor("#F29956")
        )
        val adapter = ColorAdapter(colors, onItemClick = { index ->
            binding.mandalaView.setBackgroundColorCustom(colors[index])
        }, selectedColor = Color.BLACK)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewColors)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun DroinCound() {
        val droinConstraint = listOf(
            binding.droin1, binding.droin2, binding.droin3, binding.droin4, binding.droin5,
            binding.droin6, binding.droin62, binding.droin8, binding.droin10, binding.droin12
        )

        val droinImages = listOf(
            binding.bg1, binding.bg2, binding.bg3, binding.bg4, binding.bg5,
            binding.bg6, binding.bg62, binding.bg8, binding.bg10, binding.bg12
        )

        for (i in droinConstraint.indices) {
            droinConstraint[i].setOnClickListener {
                for (j in droinImages.indices) {
                    droinImages[j].setImageResource(R.drawable.bg_droin_noselected)
                }
                droinImages[i].setImageResource(R.drawable.bg_droin_selected)
                var x: Int = 0
                x = when (i) {
                    7 -> 6
                    9 -> 10
                    10 -> 12
                    else -> i + 1
                }
                binding.mandalaView.setSymmetryCount(x)
            }
        }
    }

    private fun goneDilog() {
        binding.llDroin.gone()
        binding.llDroin2.gone()
        binding.recyclerViewColors.gone()
        binding.clPicBrush.gone()
        binding.icPen.alpha = 1.0f
    }

    override fun dataObservable() {
    }

    private fun showFoundGhost() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_new_file, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvSave = dialogView.findViewById<TextView>(R.id.tv_save)
        val ivDiscard = dialogView.findViewById<TextView>(R.id.tv_discard)
        val popupLayout = dialogView.findViewById<LinearLayout>(R.id.popupLayout)

        dialogView.setOnTouchListener { _, event ->
            val rect = Rect()
            popupLayout.getGlobalVisibleRect(rect)
            if (event.action == MotionEvent.ACTION_DOWN && !rect.contains(
                    event.rawX.toInt(),
                    event.rawY.toInt()
                )
            ) {
                dialog.dismiss()
                return@setOnTouchListener true
            }
            false
        }

        tvSave.setOnClickListener {
            saveImg()
            extracted(dialog)
        }
        ivDiscard.setOnClickListener {
            dialog.dismiss()
            binding.mandalaView.clearCanvas()
        }
        dialog.show()
    }

    private fun saveImg() {
        val cardView = findViewById<CardView>(R.id.cardView)
        cardView?.let {
            val bitmap = getBitmapFromView(it)
            saveBitmapToGallery(bitmap)
            Toast.makeText(this, getString(R.string.photo_saved_to_device), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun extracted(dialog: AlertDialog) {
        dialog.dismiss()
        binding.mandalaView.clearCanvas()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        view.draw(canvas)
        return returnedBitmap
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val filename = "mandala_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = applicationContext.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            val outputStream = resolver.openOutputStream(it)
            outputStream.use { stream ->
                if (stream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
            }
        }
    }


}