package com.example.footballapps.utils

import android.graphics.*
import com.squareup.picasso.Transformation

class PicassoCircleTransformation : Transformation {

    private var x : Int = 0
    private var y : Int = 0

    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)

        x = (source.width - size) / 2
        y = (source.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)

        if(squaredBitmap != source){
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, source.config)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        val bitmapShader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = bitmapShader
        paint.isAntiAlias = true

        val r = size / 2f
        canvas.drawCircle(r,r,r,paint)

        squaredBitmap.recycle()

        return bitmap
    }

    override fun key(): String = "circle(x=$x,y=$y)"

}