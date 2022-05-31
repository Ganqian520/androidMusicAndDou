package com.ganqian.music.viewModel

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Observable
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.ganqian.aaa.mlog
import com.ganqian.music.MusicActivity
import com.ganqian.music.api.api
import com.ganqian.music.api.http
import com.ganqian.music.api.httpDou
import com.ganqian.music.data.Cache
import com.ganqian.music.data.Song
import com.ganqian.music.data.SongList
import com.ganqian.music.page.floatingWindow.ServiceListenMusic
import com.ganqian.music.util.sp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("MutableCollectionMutableState")
@OptIn(DelicateCoroutinesApi::class)
class VmPlayer : ViewModel() {
    
    lateinit var navController: NavHostController  //导航
    lateinit var controlListen:ServiceListenMusic.Control //粘贴监听弹窗的控制器
    var isOpenListen by mutableStateOf(false) //是否打开粘贴弹窗
    var isShowPlay by mutableStateOf(false) //是否展示播放页
    var isShowWant = mutableStateOf(false) //是否展示自定义队列
    var isOpenMore = mutableStateOf(false) //列表项更多弹窗
    var angle = 0f //保存封面的旋转角度
    var pageCurrent by mutableStateOf(0) //首页vp2页数
    var statePushMusic by mutableStateOf(0) //0开始 1等待 2失败
    lateinit var mediaPlayer: MediaPlayer //播放器
    var isPlay by mutableStateOf(false) //是否播放中
    var song by mutableStateOf(Song()) //当前歌曲
    var order by mutableStateOf(0) //播放顺序 0顺序 1随机 2单曲
    var mode by mutableStateOf("net") //net dou other fm
    
    var listsNet by mutableStateOf(ArrayList<SongList>()) //网易云歌单列表
    var listWant = mutableStateListOf<Song>()//插队列表
    
    var listNet by mutableStateOf(ArrayList<Song>())  //首页net
    var listDou by mutableStateOf(ArrayList<Song>()) //首页dou
    var listOther by mutableStateOf(ArrayList<Song>()) //除了首页两种之外的
    var listDouAll by mutableStateOf(ArrayList<Song>()) //抖音全部歌曲
    var indexFromList = -1 //临时序号
    var indexNet by mutableStateOf(0) //三种列表的序号
    var indexDou by mutableStateOf(0)
    var indexOther by mutableStateOf(0)
    var indexSongList by mutableStateOf(0) //歌单序号
    var douTag by mutableStateOf(0)
    
    val timer = Timer() //定时器
    var task: TimerTask? = null
    
    var cbLyric: ((current: Int) -> Unit)? = null
    var cbProgress: ((current: Int) -> Unit)? = null
    var backPlay: (() -> Unit)? = null //播放页返回动画
    //推送歌曲到云端
    fun pushMusics(){
        if(listDouAll.size==0) return
        statePushMusic = 1
        GlobalScope.launch {
            val res = httpDou.updateMusics()
            statePushMusic = if(res) 0 else 2
        }
        
    }
    //点击列表项的更多
    fun handleItemMore(from:String,song: Song){
        sp.edit().putString("popSong",Gson().toJson(song)).apply()
        if(from=="dou"){
            isOpenMore.value = true
        }
    }
    //删除抖音歌曲
    fun deleteDouMusic(){
        val song = Gson().fromJson(sp.getString("popSong",""),Song::class.java)
        val list = ArrayList<Song>()
        for(v in listDouAll){
            if(v.id!=song.id){
                list.add(v)
            }
        }
        listDouAll = list
        sp.edit().putString("musics",Gson().toJson(listDouAll)).apply()
        isOpenMore.value = false
    }
    //开闭监听弹窗
    fun handleListen(){
        val context = MusicActivity.context
        if(isOpenListen){
            controlListen.closeAll()
            context.stopService(Intent(context,ServiceListenMusic::class.java))
        }else{
            context.bindService(Intent(context,ServiceListenMusic::class.java),object :ServiceConnection{
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    controlListen = service as ServiceListenMusic.Control
                }
                override fun onServiceDisconnected(name: ComponentName?){}
            }, Context.BIND_AUTO_CREATE)
        }
        isOpenListen = !isOpenListen
    }
    
    //设置回调
    fun setCallBack() {
        mediaPlayer.setOnCompletionListener { next() }
        task = object : TimerTask() {
            override fun run() {
                val current = mediaPlayer.currentPosition
                if (isShowPlay) {
                    cbLyric?.let { it(current) }
                    cbProgress?.let { it(current) }
                }
            }
        }
        timer.scheduleAtFixedRate(task, 0, 300)
    }
    
    //启动播放
    fun start(song_: Song) {
        song = song_
        isPlay = true
        saveHistory(song)
        when (mode) {
            "net" -> indexNet = indexFromList
            "dou" -> indexDou = indexFromList
            "other" -> indexOther = indexFromList
        }
        val url =
            if (song.platform == "net") "http://music.163.com/song/media/outer/url?id=${song.id}" else song.url
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            if (task == null) setCallBack()
        }
    }
    
    //下一首
    fun next() {
        //私人fm模式 最优先
        if (mode == "fm") {
            if (listOther.size == 0) return
            if(listOther.size>1) listOther.removeAt(0) //至少留一首
            if (listOther.size == 1) {
                GlobalScope.launch {
                    val list_ = http.getFM()
                    val list__ = ArrayList<Song>()
                    list_.forEach { it1->
                        listOther.forEach { it2->
                            if(it1.id!=it2.id) list__.add(it1)
                        }
                    }
                    listOther = list__
                }
            }
            start(listOther[0])
            return
        }
        //插队列表
        if (listWant.size != 0) {
            start(listWant[0])
            listWant.removeAt(0)
            return
        }
        //听从order
        if (order == 2) {
            mediaPlayer.start()
            return
        }
        if (mode == "net") {
            if (order == 0) indexNet = if (indexNet == listNet.size - 1) 0 else indexNet + 1
            if (order == 1) indexNet = (0..listNet.size - 1).random()
            indexFromList = indexNet
            start(listNet[indexNet])
        }
        if (mode == "dou") {
            if (order == 0) indexDou = if (indexDou == listDou.size - 1) 0 else indexDou + 1
            if (order == 1) indexDou = (0..listDou.size - 1).random()
            indexFromList = indexDou
            start(listDou[indexDou])
        }
        if (mode == "other") {
            if (order == 0) indexOther = if (indexOther == listOther.size - 1) 0 else indexOther + 1
            if (order == 1) indexOther = (0..listOther.size - 1).random()
            indexFromList = indexOther
            start(listOther[indexOther])
        }
    }
    
    //上一首
    fun last() {
    
    }
    
    //恢复
    fun resume() {
        isPlay = true
        mediaPlayer.start()
    }
    
    //暂停
    fun pause() {
        isPlay = false
        mediaPlayer.pause()
    }
    
    //跳转
    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }
    
    //处理播放历史
    fun saveHistory(song: Song) {
        val list_ = sp.getString("history", "")!!
        var list = ArrayList<Song>()
        if (list_ != "") list =
            Gson().fromJson(list_, object : TypeToken<ArrayList<Song>>() {}.type)
        var index = -1
        for ((i, v) in list.withIndex()) {
            if (v.name == song.name && v.platform == song.platform && v.id == song.id) {
                index = i
                break
            }
        }
        if (index != -1) list.removeAt(index)
        list.add(0, song)
        if (list.size > 100) list.removeAt(list.size - 1)
        sp.edit().putString("history", Gson().toJson(list)).apply()
    }
    
    //存入缓存
    fun saveCache() {
        val cache = Cache()
        cache.douTag = douTag
        cache.indexDou = indexDou
        cache.indexNet = indexNet
        cache.order = order
        cache.indexSongList = indexSongList
        cache.song = song
        sp.edit().putString("vmPlayer", Gson().toJson(cache)).apply()
    }
    
    //获取缓存
    fun getCache() {
        val cache_ = sp.getString("vmPlayer", "")
        if (cache_ != "") {
            val cache = Gson().fromJson(cache_, Cache::class.java)
            douTag = cache.douTag
            indexDou = cache.indexDou
            indexNet = cache.indexNet
            order = cache.order
            indexSongList = cache.indexSongList
            song = cache.song
        }
    }
}