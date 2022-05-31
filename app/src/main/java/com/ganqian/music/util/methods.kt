package com.ganqian.music.util

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import com.ganqian.music.data.Lyric
import com.ganqian.music.data.Song
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object util {
    //二分法查找中间歌词序号
    fun findMiddle(list:ArrayList<Lyric>,currentTime:Int):Int{
        var start = 0
        var end = list.size-1
        while (true){
            if(end-start<=1) return start
            val middle = (start+end)/2
            if(currentTime<list[middle].millisecond){
                end = middle
            }else{
                start = middle
            }
        }
    }
    //dp互转px
    fun sizeTo(p1:Int,isD2P:Boolean=true):Float{
        val density = Resources.getSystem().displayMetrics.density
        return p1*density
    }
    //根据中心点绘制文字
    fun myDrawText(
        text: String?,
        centerX: Float,
        centerY: Float,
        canvas: Canvas,
        paint: Paint
    ) {
        val textWidth = paint.measureText(text)
        val fontMetrics = paint.fontMetrics
        val baselineY = centerY + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        canvas.drawText(text!!, centerX - textWidth / 2, baselineY, paint)
    }
    //时间转字符串
    fun transTime(seconds: Int): String {
        val minute = seconds / 60
        val second = seconds % 60
        var str_second = ""
        var str_minute = ""
        str_second = if (second > 9) {
            "" + second
        } else {
            "0$second"
        }
        str_minute = if (minute > 9) {
            "" + minute
        } else if (minute == 0) {
            "00"
        } else {
            "0$minute"
        }
        return "$str_minute:$str_second"
    }
    //处理歌取列表
    fun handleNetSongs(list_:JsonArray,isFM:Boolean=false):ArrayList<Song>{
        val list = ArrayList<Song>()
        val author = if(isFM) "artists" else "ar"
        val duration = if(isFM) "duration" else "dt"
        val album = if(isFM) "album" else "al"
        for(v in list_){
            val song = Song()
            val obj = v.asJsonObject
            song.id = obj.get("id").asString
            song.name = obj.get("name").asString
            song.duration = obj.get(duration).asInt/1000
            song.platform = "net"
            song.img = obj.getAsJsonObject(album).get("picUrl").asString
            var str = ""
            val ar =obj.getAsJsonArray(author)
            for(v2 in ar){
              str = "$str/${v2.asJsonObject.get("name").asString}"
            }
            song.author = str.substring(1)
            list.add(song)
        }
        return list
    }
}