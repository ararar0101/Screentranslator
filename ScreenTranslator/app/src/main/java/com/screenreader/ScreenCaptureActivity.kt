package com.screenreader

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ScreenCaptureActivity : AppCompatActivity() {

    private lateinit var screenshot: ImageView
    private lateinit var selectionOverlay: View
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var isSelecting = false
    private lateinit var screenshotBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_capture)

        screenshot = findViewById(R.id.screenshot)
        selectionOverlay = findViewById(R.id.selectionOverlay)

        // Capture screenshot
        captureScreen()

        // Setup touch listener for selection
        setupSelectionListener()
    }

    private fun captureScreen() {
        // Get root view and create bitmap from it
        val rootView = window.decorView.rootView
        screenshotBitmap = Bitmap.createBitmap(
            rootView.width,
            rootView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(screenshotBitmap)
        rootView.draw(canvas)

        screenshot.setImageBitmap(screenshotBitmap)
    }

    private fun setupSelectionListener() {
        selectionOverlay.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                    isSelecting = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isSelecting) {
                        endX = event.x
                        endY = event.y
                        drawSelectionRect()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isSelecting) {
                        endX = event.x
                        endY = event.y
                        isSelecting = false
                        processSelectedArea()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun drawSelectionRect() {
        selectionOverlay.setBackgroundColor(0x00000000) // Transparent
        selectionOverlay.invalidate()
        
        // Create custom drawable with selection rectangle
        val customBitmap = Bitmap.createBitmap(
            selectionOverlay.width,
            selectionOverlay.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(customBitmap)
        
        // Draw semi-transparent overlay
        canvas.drawColor(0x80000000.toInt())
        
        // Clear rectangle area
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        
        val left = minOf(startX, endX)
        val top = minOf(startY, endY)
        val right = maxOf(startX, endX)
        val bottom = maxOf(startY, endY)
        
        canvas.drawRect(left, top, right, bottom, paint)
        
        // Draw border
        paint.xfermode = null
        paint.style = Paint.Style.STROKE
        paint.color = 0xFF2196F3.toInt()
        paint.strokeWidth = 5f
        canvas.drawRect(left, top, right, bottom, paint)
        
        selectionOverlay.background = android.graphics.drawable.BitmapDrawable(resources, customBitmap)
    }

    private fun processSelectedArea() {
        val left = minOf(startX, endX).toInt()
        val top = minOf(startY, endY).toInt()
        val right = maxOf(startX, endX).toInt()
        val bottom = maxOf(startY, endY).toInt()

        if (right - left < 10 || bottom - top < 10) {
            Toast.makeText(this, R.string.select_area, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Crop bitmap to selected area
        val croppedBitmap = Bitmap.createBitmap(
            screenshotBitmap,
            left,
            top,
            right - left,
            bottom - top
        )

        // Show progress
        Toast.makeText(this, R.string.recognizing, Toast.LENGTH_SHORT).show()

        // Perform OCR
        performOCR(croppedBitmap)
    }

    private fun performOCR(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                
                val result = withContext(Dispatchers.IO) {
                    recognizer.process(inputImage).await()
                }

                val recognizedText = result.text

                if (recognizedText.isNotEmpty()) {
                    // Translate text
                    translateText(recognizedText)
                } else {
                    Toast.makeText(
                        this@ScreenCaptureActivity,
                        R.string.no_text_found,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ScreenCaptureActivity,
                    "${getString(R.string.error_occurred)}: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun translateText(text: String) {
        Toast.makeText(this, R.string.translating, Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val translatedText = withContext(Dispatchers.IO) {
                    TranslationHelper.translate(text, MainActivity.selectedTargetLanguage)
                }

                showTranslationResult(text, translatedText)
            } catch (e: Exception) {
                Toast.makeText(
                    this@ScreenCaptureActivity,
                    "${getString(R.string.error_occurred)}: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun showTranslationResult(originalText: String, translatedText: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_translation_result, null)
        
        val originalTextView = dialogView.findViewById<android.widget.TextView>(R.id.originalText)
        val translatedTextView = dialogView.findViewById<android.widget.TextView>(R.id.translatedText)
        val copyButton = dialogView.findViewById<android.widget.Button>(R.id.copyButton)
        val closeButton = dialogView.findViewById<android.widget.Button>(R.id.closeButton)

        originalTextView.text = originalText
        translatedTextView.text = translatedText

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        copyButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("translation", translatedText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, R.string.copy, Toast.LENGTH_SHORT).show()
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.setOnDismissListener {
            finish()
        }

        dialog.show()
    }
}
