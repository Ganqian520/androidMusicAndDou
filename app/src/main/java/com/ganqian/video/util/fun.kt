package com.ganqian.video.util

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.ganqian.aaa.mlog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


//随机颜色
fun randomColor(): Color {
    val r = (0..1000).random().toFloat()/1000
    val g = (0..1000).random().toFloat()/1000
    val b = (0..1000).random().toFloat()/1000
    return Color(r,g,b,1f)
}
//随机bush
fun randomBush():Brush {
    return Brush.verticalGradient(
       0f to randomColor(),
       1f to randomColor()
    )
}
//根据时间戳返回myDate
fun getMyDate(timeStamp:Any):MyDate{
    val myDate = MyDate()
    myDate.year =  SimpleDateFormat("yyyy", Locale.CHINA).format(timeStamp).toInt()
    myDate.month =  SimpleDateFormat("MM", Locale.CHINA).format(timeStamp).toInt()
    return myDate
}
//生成时间筛选数组
fun produceDates():ArrayList<MyDate>{
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH) + 1
    val startYear = 2017
    val list = ArrayList<MyDate>()
    for (i in 0 until (year - startYear) * 13) {
        val remainder = i % 13
        val quotient = i / 13
        val myDate = MyDate()
        myDate.year = startYear + quotient
        if (remainder != 12){
            myDate.month = remainder + 1
            myDate.show = myDate.month
        } else{
            myDate.month = -1
            myDate.show = myDate.year
        }
        list.add(myDate)
    }
    for (i in 1..month) list.add(MyDate(i,year,i))
    list.add(MyDate(year,year,-1))
    list.reverse()
    return list
}