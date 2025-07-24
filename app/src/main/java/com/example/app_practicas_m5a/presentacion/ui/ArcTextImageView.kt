package com.example.app_practicas_m5a.presentacion.ui


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ArcTextImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2E7D32")
        textSize = 70f
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val paintImage = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmap: Bitmap? = null

    private val path = Path()

    fun setImageBitmap(bmp: Bitmap) {
        bitmap = bmp
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width.coerceAtMost(height) / 2f) * 0.75f

        // Dibuja la imagen en el centro
        bitmap?.let {
            // En lugar de usar imgSize calculado dinámicamente:
            val imgSize = 600f // Fijo, tu imagen es 600x600

            val scaledBmp = Bitmap.createScaledBitmap(it, imgSize.toInt(), imgSize.toInt(), false)
            val left = centerX - imgSize / 2
            val top = centerY - imgSize / 2
            canvas.drawBitmap(scaledBmp, left, top, paintImage)

        }

        // Arco superior para texto
        path.reset()
        val rectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        path.addArc(rectF, 180f, 180f)

        // Dibuja texto "Kawsana" sobre el arco
        val text = "KAWSANA"
        canvas.drawTextOnPath(text, path, 0f, 0f, paintText)
    }
}

