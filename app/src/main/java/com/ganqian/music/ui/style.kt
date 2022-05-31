package com.ganqian.music.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material.lightColors
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.ganqian.compose.R

val aliFont = Font(resId = R.font.iconfont)
val aliFontFamily = FontFamily(aliFont)

val iconStyle = TextStyle(
    fontFamily = aliFontFamily,
    color = Color.White,
    textAlign = TextAlign.Center,
    fontSize = 25.sp
)

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = Color.White,
//        textAlign = TextAlign.Center,
    )
)

@Composable
fun Theme_(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = Color.White,
            secondary = Color.White
        ),
        typography = Typography,
        content = content
    )
}