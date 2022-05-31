package com.ganqian.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.ganqian.aaa.aautil
import com.ganqian.aaa.mlog
import com.ganqian.music.MusicActivity
import com.ganqian.video.util.sp

import com.danikula.videocache.HttpProxyCacheServer

@SuppressLint("StaticFieldLeak")
class VideoActivity : ComponentActivity() {
    
    companion object{
        lateinit var context: Context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        aautil.tranStatusBar(this)
        val vm = ViewModelProvider(this).get(Vm::class.java)
        setContent {
            App(vm)
        }
    }
}
