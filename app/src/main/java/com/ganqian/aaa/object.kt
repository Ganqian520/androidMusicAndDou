package com.ganqian.aaa

import android.app.Activity
import android.content.Context
import com.ganqian.music.MusicActivity
import android.content.res.Resources

//通用缓存
val spAll = MyApplication.appContext.getSharedPreferences("", Context.MODE_PRIVATE)
//屏幕信息
object screen{
    val sbH = Resources.getSystem()
        .getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"))
    val h = Resources.getSystem().displayMetrics.heightPixels
    val w = Resources.getSystem().displayMetrics.widthPixels
    val density = Resources.getSystem().displayMetrics.density
    val allH = h+sbH
}
