package com.ganqian.aaa

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
//找最小值
fun IntArray.minIndex() : Int {
    var i = 0
    var min = Int.MAX_VALUE
    this.forEachIndexed { index, e ->
        if (e<min){
            min = e
            i = index
        }
    }
    return i
}

val Dp.d2p: Int
    get() = (this.value*Resources.getSystem().displayMetrics.density+0.5).toInt()
val Int.d2p :Int
    get() = (this*Resources.getSystem().displayMetrics.density+0.5).toInt()
val Double.d2p :Int
    get() = (this*Resources.getSystem().displayMetrics.density+0.5).toInt()

val Int.p2d: Int
    get() = (this/Resources.getSystem().displayMetrics.density+0.5).toInt()
val Double.p2d:Int
    get() = (this/Resources.getSystem().displayMetrics.density+0.5).toInt()
//画阴影
fun Modifier.advancedShadow(
    color: Color = Color.Black,
    alpha: Float = 0.3f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = 10.dp,
    offsetY: Dp = 10.dp,
    offsetX: Dp = 10.dp
) = drawBehind {
    
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()
    
    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowBlurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
    }
}