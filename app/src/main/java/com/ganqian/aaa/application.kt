package com.ganqian.aaa

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.ganqian.video.VideoActivity

class MyApplication: Application() {
    
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
    }
    
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
//        spAll.edit().putString("activity","music").apply()
        val activityStr = spAll.getString("activity","music")
        if(activityStr=="video"){
            val intent = Intent(this,VideoActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    
}