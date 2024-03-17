package com.rahmadev.tflite.objectdetection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.text.NumberFormat
import java.util.LinkedList
import kotlin.math.max

class Overlay(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var results: List<Detection> = LinkedList<Detection>()
    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    init {
        initPaints()
    }

    private fun initPaints() {
        boxPaint.apply {
            color = ContextCompat.getColor(context, R.color.bounding_box_color)
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        textBackgroundPaint.apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            textSize = 50f
        }

        textPaint.apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = 50f
        }
    }

    fun setResult(
        detectionResult: MutableList<Detection>,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResult
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val boundingBox = result.boundingBox

            val left = boundingBox.left * scaleFactor
            val top = boundingBox.top * scaleFactor
            val right = boundingBox.right * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor

            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            val drawableText = "${result.categories[0].label} " + NumberFormat.getPercentInstance()
                .format(result.categories[0].score)

            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                left,
                top,
                left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
        }
    }

    fun clear() {
        boxPaint.reset()
        textBackgroundPaint.reset()
        textPaint.reset()
        invalidate()
        initPaints()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}