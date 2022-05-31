package com.ganqian.music

//import com.ganqian.music.util.sp
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.ganqian.aaa.aautil
import com.ganqian.aaa.mlog
import com.ganqian.aaa.spAll
import com.ganqian.compose.R
import com.ganqian.music.api.OkHttp
import com.ganqian.music.api.api
import com.ganqian.music.api.dou
import com.ganqian.music.api.uidNet
import com.ganqian.music.component.App
import com.ganqian.music.data.UserNet
import com.ganqian.music.service.ServicePlayer
import com.ganqian.music.util.NoticeBar
import com.ganqian.music.viewModel.VmLogin
import com.ganqian.music.viewModel.VmPlayer
import com.ganqian.pianoWindow.pianoWindowActivity
import com.ganqian.webview.WebviewActivity
import com.google.gson.Gson

//import com.danikula.videocache.HttpProxyCacheServer

class MusicActivity : ComponentActivity() {
    lateinit var vm:VmPlayer
    lateinit var vmLogin: VmLogin
    
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
//        fun getProxy():HttpProxyCacheServer{
//
//        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        val str = spAll.getString("activity","music")
        if(str!="music") {
            finish()
            return
        }
//        val notificationManager = getSystemService(NotificationManager::class.java);
//        val channel = NotificationChannel("id","name",NotificationManager.IMPORTANCE_DEFAULT)
//        val remoteView = RemoteViews(packageName, R.layout.notifycation_music)
//        val notigication = NotificationCompat.Builder(this,"id")
//            .setCustomContentView(remoteView)
//            .build()
//        notificationManager.createNotificationChannel(channel)
//        notificationManager.notify(0,notigication)
        vm = ViewModelProvider(this).get(VmPlayer::class.java)
        vmLogin = ViewModelProvider(this).get(VmLogin::class.java)
        aautil.tranStatusBar(this)
        connectPlayService()
        OkHttp.init(this)
        api.init()
        dou.init()
        getCache()
        vm.getCache()
        vmLogin.getCache()
        aautil.getPermisson(this)
//        loginQQ(this)
        setContent {
            App(this,vm,vmLogin)
        }
    }
    
    override fun onBackPressed() {
        if(vm.isShowPlay){
            vm.backPlay?.let { it() }
        }else{
            super.onBackPressed()
        }
    }
    
    //获取缓存
    fun getCache(){
//        val userNet_ = utilCache.sp.getString("userNet","")
//        if(userNet_!=""){
//            val userNet = Gson().fromJson(userNet_,UserNet::class.java)
//            vmLogin.user = userNet
//            uidNet = userNet.id
//        }
    }
    
    //启动播放服务
    fun connectPlayService(){
        val conn = object :ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                val control:ServicePlayer.Control = p1 as ServicePlayer.Control
                vm.mediaPlayer = control.mediaPlayer
            }
            override fun onServiceDisconnected(p0: ComponentName?) {
            
            }
        }
        bindService(Intent(this,ServicePlayer::class.java),conn, BIND_AUTO_CREATE)
    }

}


