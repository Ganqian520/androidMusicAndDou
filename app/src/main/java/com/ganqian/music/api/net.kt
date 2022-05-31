package com.ganqian.music.api
import android.annotation.SuppressLint
import android.content.Context
import com.ganqian.aaa.mlog
import com.ganqian.music.data.*
import com.ganqian.music.util.sp
import com.ganqian.music.util.util
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

const val NET = "https://ganqian1.vercel.app"

var uidNet = "" //网易云用户id

interface ServiceNet {
    //会员歌曲播放地址
    @GET("/song/url?id")
    suspend fun getMusicUrl(@Query("id")id:String):JsonObject
    //推荐歌单 30条
    @GET("/personalized")
    suspend fun personalized():JsonObject
    //日推
    @GET("/recommend/songs")
    suspend fun getDayAdvice(@Query("uid")uid:String= uidNet):JsonObject
    //点赞评论
    @GET("/comment/like")
    suspend fun likeComment(@Query("id")id:String, @Query("cid")cid:String, @Query("t")t:String="1", @Query("type")type:String="0"):JsonObject
    //获取评论
    @GET("/comment/music")
    suspend fun getComment(@Query("id")id:String):JsonObject
    //添加我喜欢
    @GET("/like")
    suspend fun addLike(@Query("id")id:String):JsonObject
    //私人FM
    @GET("/personal_fm")
    suspend fun getFM():JsonObject
    //预搜词
    @GET("/search/suggest")
    suspend fun getPrepareKeywords(@Query("keywords")input:String,@Query("type")type:String="mobile"):JsonObject
    //热搜
    @GET("/search/hot/detail")
    suspend fun getHotKeywords():JsonObject
    //搜索
    @GET("/cloudsearch")
    suspend fun search(@Query("keywords")keywords:String):JsonObject
    //歌词
    @GET("/lyric")
    suspend fun getLyric(@Query("id")id:String):JsonObject
    //歌曲列表
    @GET("/playlist/detail")
    suspend fun getSongs(@Query("id")id:String):JsonObject
    //歌单列表
    @GET("/user/playlist")
    suspend fun getSongLists(@Query("uid") uid:String): JsonObject
    //电话密码登录
    @GET("/login/cellphone")
    suspend fun loginPassword(@Query("phone")phone:String,@Query("password")password:String): JsonObject
    //歌单标签列表
    @GET("/playlist/highquality/tags")
    suspend fun highqualityTags():JsonObject
    //歌单列表
    @GET("/top/playlist/highquality")
    suspend fun topPlaylistHighquality(@Query("cat")name:String):JsonObject
    //榜单摘要列表
    @GET("/toplist/detail")
    suspend fun toplistDetail():JsonObject
}

object http {
    //获取音乐播放地址
    suspend fun getMusicUrl(id: String):String{
        val jo = api.instance.getMusicUrl(id)
        var url = ""
        try {
          url = jo.getAsJsonArray("data")[0].asJsonObject.get("url").asString
        }catch (e:Exception){System.out.println("捕获异常：$e")}
        return url
    }
    //榜单摘要列表
    suspend fun toplistDetail():ArrayList<Gedan>{
        val list = ArrayList<Gedan>()
        val res = api.instance.toplistDetail()
        val list_ = res.getAsJsonArray("list")
        for((i,v_) in list_.withIndex()){
            val v = v_.asJsonObject
            val gedan = Gedan()
            try {
                gedan.name = v.get("name").asString
                gedan.description = v.get("description").asString
                gedan.id = v.get("id").asString
                gedan.img = v.get("coverImgUrl").asString
                gedan.updateFrequency = v.get("updateFrequency").asString
                if(i<4){
                    val arr_ = v.getAsJsonArray("tracks")
                    var str = ""
                    for(item in arr_){
                        str = "$str,${item.asJsonObject.get("first").asString}\n${item.asJsonObject.get("second").asString}"
                    }
                    gedan.abstract = str.substring(1,str.length-1)
                }
                
            }catch (e:Exception){System.out.println("捕获异常：$e")}
            list.add(gedan)
        }
        return list
    }
    //歌单列表
    suspend fun topPlaylistHighquality(name: String):ArrayList<Gedan>{
        var list = ArrayList<Gedan>()
        val res = api.instance.topPlaylistHighquality(name = name)
        val list_ = res.getAsJsonArray("playlists")
        for(v_ in list_){
            val v = v_.asJsonObject
            val gedan = Gedan()
            gedan.name = v.get("name").asString
            gedan.id = v.get("id").asString
            gedan.img = v.get("coverImgUrl").asString
            gedan.playCount = v.get("playCount").asString
            list.add(gedan)
        }
        return list
    }
    //歌单标签
    suspend fun highqualityTags():ArrayList<Gedan>{
        val list = ArrayList<Gedan>()
        val res = api.instance.highqualityTags()
        val list_ = res.getAsJsonArray("tags")
        for(v_ in list_){
            val v = v_.asJsonObject
            val gedan = Gedan()
            gedan.name = v.get("name").asString
            gedan.id = v.get("id").asString
            list.add(gedan)
        }
        return list
    }
    //获取推荐歌单
    suspend fun personalized():ArrayList<Gedan>{
        val list = ArrayList<Gedan>()
        val res = api.instance.personalized()
        val list_ = res.getAsJsonArray("result")
        for(v_ in list_){
            val v = v_.asJsonObject
            val gedan = Gedan()
            gedan.name = v.get("name").asString
            gedan.id = v.get("id").asString
            gedan.playCount = v.get("playCount").asString
            gedan.img = v.get("picUrl").asString
            list.add(gedan)
        }
        return list
    }
    //获取日推
    suspend fun getDayAdvice():ArrayList<Song>{
        val res:JsonObject
        val now = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now())
        val last = sp.getString("dayAdvice","")
        res = api.instance.getDayAdvice()
        sp.edit().putString("dayAdvice",now).apply()
        val list_ = res.getAsJsonObject("data").getAsJsonArray("dailySongs")
        return  util.handleNetSongs(list_)
    }
    //获取评论
    suspend fun getComment(id: String):ArrayList<Comment>{
        val list = ArrayList<Comment>()
        val res = api.instance.getComment(id)
        val list1 = res.getAsJsonArray("hotComments")
        val list2 = res.getAsJsonArray("comments")
        fun handle(ja:JsonArray, out:ArrayList<Comment>){
            for(v_ in ja){
                val v = v_.asJsonObject
                val comment = Comment()
                comment.name = v.getAsJsonObject("user").get("nickname").asString
                comment.img = v.getAsJsonObject("user").get("avatarUrl").asString
                comment.likes = v.get("likedCount").asInt
                comment.liked = v.get("liked").asBoolean
//                comment.time = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(v.get("time").asLong)
                comment.time = v.get("timeStr").asString
                comment.id = v.get("commentId").asString
                comment.content = v.get("content").asString
                out.add(comment)
            }
        }
        handle(list1,list)
        handle(list2,list)
        return list
    }
    //添加我喜欢
    suspend fun addLike(id:String):Boolean{
        val res = api.instance.addLike(id)
        val code = res.get("code").asInt
        return code==200
    }
    //私人fm
    suspend fun getFM():ArrayList<Song>{
        val res = api.instance.getFM()
        val list_ = res.getAsJsonArray("data")
        return util.handleNetSongs(list_,true)
    }
    //预搜关键词
    suspend fun getPrepareKeywords(input: String):ArrayList<String>{
        val list = ArrayList<String>()
        val res = api.instance.getPrepareKeywords(input)
        val list_ = res.getAsJsonObject("result").getAsJsonArray("allMatch")
        for(v in list_){
            list.add(v.asJsonObject.get("keyword").asString)
        }
        return list
    }
    //热搜关键词
    suspend fun getHotKeywords():ArrayList<String>{
        val res = api.instance.getHotKeywords()
        val list_ = res.getAsJsonArray("data")
        val list = ArrayList<String>()
        for(v in list_){
            list.add(v.asJsonObject.get("searchWord").asString)
        }
        return list
    }
    
    //搜索网易云歌曲
    suspend fun searchNet(keywords:String):ArrayList<Song>{
        val res = api.instance.search(keywords = keywords)
        val list_ = res.getAsJsonObject("result").getAsJsonArray("songs")
        return util.handleNetSongs(list_ = list_)
    }
    
    //获取歌词
    suspend fun getLyric(id: String): ArrayList<Lyric> {
        val res =  api.instance.getLyric(id)
        val str = res.getAsJsonObject("lrc").get("lyric").asString
        val list_ = str.split("\n")
        val list = ArrayList<Lyric>()
        for (v in list_) {
            val lyric = Lyric()
            val matcher = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d{2})").matcher(v)
            val matcher1 = Pattern.compile("(])(.*)").matcher(v)
            if (matcher.find() and matcher1.find()) {
                val mm = matcher.group(1)
                val ss = matcher.group(2)
                val milli = matcher.group(3)
                val content = matcher1.group(2)!!
                if(content=="") continue
                lyric.format = "$mm:$ss"
                lyric.millisecond =  mm!!.toInt() * 60000 + ss!!.toInt() * 1000 + milli!!.toInt() * 10
                lyric.content = content
                list.add(lyric)
            }
        }
        return list
    }
    
    //获取歌单里的歌曲列表
    suspend fun getSongs(id: String, isFresh:Boolean = false): ArrayList<Song> {
        var list: ArrayList<Song>
        val listStorage = sp.getString("netList$id","")
        if(!isFresh && listStorage!=""){
            list = Gson().fromJson(listStorage,object :TypeToken<ArrayList<Song>>(){}.type)
        }else{
            val res = api.instance.getSongs(id)
            val list_ = res.getAsJsonObject("playlist").getAsJsonArray("tracks")
            list = util.handleNetSongs(list_)
            sp.edit().putString("netList$id",Gson().toJson(list)).apply()
        }
        return list
    }
    
    //获取歌单列表
    suspend fun getSongLists(uid: String, isFresh: Boolean = false): ArrayList<SongList> {
        var list = ArrayList<SongList>()

        val listStorage = sp.getString("geDanList","")!!
        if(!isFresh && listStorage!=""){
            list = Gson().fromJson(listStorage,object :TypeToken<ArrayList<SongList>>(){}.type)
        }else{
            val jo = api.instance.getSongLists(uid)
            val list_ = jo.getAsJsonArray("playlist")
            for ((i, v) in list_.withIndex()) {
                val id = v.asJsonObject.get("id").asString
                var name = v.asJsonObject.get("name").asString
                val img = v.asJsonObject.get("coverImgUrl").asString
                if (i == 0) name = "我喜欢"
                list.add(SongList(id, name, img))
            }
            sp.edit().putString("geDanList",Gson().toJson(list)).apply()
        }
        return list
    }
}

object api {
    lateinit var instance:ServiceNet
    fun init(){
        val retrofit = Retrofit.Builder()
            .client(OkHttp.client)
            .baseUrl(NET)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        instance = retrofit.create(ServiceNet::class.java)
    }
}




