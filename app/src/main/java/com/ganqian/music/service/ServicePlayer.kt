package com.ganqian.music.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class ServicePlayer : Service() {
    
    val mediaPlayer = MediaPlayer()
    
    override fun onBind(p0: Intent?): IBinder? {
        return Control(mediaPlayer)
    }
    
    class Control(val mediaPlayer:MediaPlayer) :Binder()
    
}