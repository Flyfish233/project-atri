package com.flyfish233.sleeptaffy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val SHADOW_RADIUS = 6f


class AtoriLegacy : CanvasWatchFaceService() {

    private val backgroundImages = arrayOf(
        R.drawable.frame_001,
        R.drawable.frame_002,
        R.drawable.frame_003,
        R.drawable.frame_004,
        R.drawable.frame_005,
    )

    private var currentBackgroundIndex = 0

    private lateinit var timePaintW: Paint
    private lateinit var timePaintR: Paint


    private lateinit var backgroundPaint: Paint
    private lateinit var backgroundBitmap: Bitmap
    private var time: Calendar = Calendar.getInstance()

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    inner class Engine : CanvasWatchFaceService.Engine() {
        private val updateTimeHandler = Handler()
        private val updateTimeRunnable = object : Runnable {
            override fun run() {
                time.timeInMillis = System.currentTimeMillis()
                invalidate()
                updateTimeHandler.postDelayed(this, 1000)
            }

        }

        private val updateBackgroundRunnable = object : Runnable {
            override fun run() {
                if (isInAmbientMode) {
                    backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.aod)
                    invalidate()
                    updateTimeHandler.postDelayed(this, 2000)
                } else {
                    currentBackgroundIndex = (currentBackgroundIndex + 1) % backgroundImages.size
                    backgroundBitmap = BitmapFactory.decodeResource(
                        resources, backgroundImages[currentBackgroundIndex]
                    )
                    invalidate()
                    updateTimeHandler.postDelayed(this, 300)
                }
            }
        }

        override fun onCreate(holder: SurfaceHolder) {

            super.onCreate(holder)

            setWatchFaceStyle(
                WatchFaceStyle.Builder(this@AtoriLegacy).setAcceptsTapEvents(false)
                    // Modified in 1.1: setHideStatusBar and setHideNotificationIndicator
                    // If you want, you can set them to true to hide the status and notification
                    // optional. If you don't set them, they will be false by default.
                    // Build yourself instead of Onion Store 1.0 to see the difference.
                    // "deprecated" in AndroidX / Jetpack and Watch Face Format
                    .setHideStatusBar(true).setHideNotificationIndicator(true)
                    // End of modification
                    .build()
            )

            timePaintR = Paint().apply {
                textSize = 80f
                color = Color.rgb(243, 56, 83)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                setShadowLayer(SHADOW_RADIUS, 3f, 3f, Color.BLACK)
                typeface = Typeface.createFromAsset(assets, "vt323_regular.ttf")
            }

            timePaintW = Paint().apply {
                textSize = 80f
                color = Color.WHITE
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                setShadowLayer(SHADOW_RADIUS, 3f, 3f, Color.BLACK)
                typeface = Typeface.createFromAsset(assets, "vt323_regular.ttf")
            }


            backgroundPaint = Paint().apply {
                isAntiAlias = true

            }

            updateTimeHandler.post(updateBackgroundRunnable)
            updateTimeHandler.post(updateTimeRunnable)

        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {

            Paint()

            val date = Date()
            val formatterHH = SimpleDateFormat("hh", Locale.getDefault())
            val formattedHH = formatterHH.format(date)

            val formatterMM = SimpleDateFormat("mm", Locale.getDefault())
            val formattedMM = formatterMM.format(date)

            val width = bounds.width()
            val height = bounds.height()

            val scaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, true)
            canvas.drawBitmap(scaledBitmap, 0f, 0f, backgroundPaint)

            canvas.drawText(
                formattedHH,
                bounds.centerX().toFloat().plus(100),
                bounds.centerY().toFloat().minus(20),
                timePaintR
            )

            canvas.drawText(
                formattedMM,
                bounds.centerX().toFloat().plus(100),
                bounds.centerY().toFloat().plus(50),
                timePaintW
            )
        }


        override fun onDestroy() {
            updateTimeHandler.removeCallbacks(updateTimeRunnable)
            updateTimeHandler.removeCallbacks(updateBackgroundRunnable)
            super.onDestroy()
        }
    }
}
