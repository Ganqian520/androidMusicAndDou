package com.ganqian.video

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.ganqian.aaa.mlog
import com.ganqian.video.api.http
import com.ganqian.video.page.floatingWindow.ServiceFloatVideo
import com.ganqian.video.util.MyDate
import com.ganqian.video.util.Video
import com.ganqian.video.util.getMyDate
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("MutableCollectionMutableState")
class Vm:ViewModel(){
    lateinit var navController: NavHostController
    lateinit var pagerState: PagerState
    lateinit var control: ServiceFloatVideo.Control
    var uid by mutableStateOf("")
    var myDate by mutableStateOf(MyDate()) //用于筛选
    var indexVideo by mutableStateOf(0) //播放序号
    var indexTag by mutableStateOf(0) //标签序号
    var indexDate by mutableStateOf(-1) //选中日期序号 用于高亮显示
    var allVideos by mutableStateOf(ArrayList<Video>())
    var playVideos by mutableStateOf(ArrayList<Video>())
    var tags by mutableStateOf(ArrayList<String>())
    var statePush by mutableStateOf(0) //已 中 败
    var isFloatService by mutableStateOf(false)
    var isChooseDate by mutableStateOf(false)
    
    //删除视频
    fun deleteVideo(video:Video){
    
    }
    //筛选视频
    fun screenVideo(i:Int,date: MyDate):ArrayList<Video>{
        var list = ArrayList<Video>()
        if (i == 0) {
            list = allVideos
        } else if (i == list.size - 1) {
            list = allVideos.filter { it.tag == "" || it.tag == "待定" } as ArrayList<Video>
        } else {
            list = allVideos.filter { it.tag == tags[i] } as ArrayList<Video>
        }
        if(isChooseDate && indexDate!=-1){
            list = list.filter {
                var flag = false
                val myDate_ = getMyDate(it.createTime*1000)
                if(date.month==-1){
                    flag = myDate_.year == date.year
                }else {
                    flag = myDate_.year== date.year && myDate_.month== date.month
                }
                flag
            } as ArrayList<Video>
        }
        return list
    }
    
    //开闭弹窗服务
    fun handleService(){
        val context = VideoActivity.context
        if(isFloatService){
            control.closeAll()
            context.stopService(Intent(context,ServiceFloatVideo::class.java))
        }else{
            context.bindService(Intent(context,ServiceFloatVideo::class.java),object :ServiceConnection{
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    control = service as ServiceFloatVideo.Control
                }
                override fun onServiceDisconnected(name: ComponentName?) {}
            }, Context.BIND_AUTO_CREATE)
        }
        isFloatService =  !isFloatService
    }
    
    //推送视频
    @OptIn(DelicateCoroutinesApi::class)
    fun pushVideos(){
        if(statePush==1) return
        statePush = 1
        GlobalScope.launch {
            val isOk = http.pushVideos()
            statePush = if(isOk) 0 else 2
        }
    }
}