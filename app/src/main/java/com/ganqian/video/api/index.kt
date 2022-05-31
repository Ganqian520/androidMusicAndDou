package com.ganqian.video.api

import com.ganqian.aaa.mlog
import com.ganqian.aaa.spAll
import com.ganqian.video.util.Video
import com.ganqian.video.util.sp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

const val DOU = "https://ca448d14-fda5-4d8f-9279-3f4896d8f854.bspapp.com"

interface Service {
    //推送视频
    @GET("all/video?action=updateVideos")
    suspend fun updateVideos(@Query("uid")uid:String, @Query("offset")offset: Int, @Query("videos")videos:ArrayList<Video>):JsonObject
    //处理分享链接
    @GET("/all/handleShare")
    suspend fun handleShare(@Query("share")share:String):JsonObject
    @POST("/all/video")
    suspend fun pushVideos(@Body map:HashMap<String,Any>):JsonObject
    //获取标签
    @GET("/all/video?action=tags")
    suspend fun tags(@Query("uid")uid: String):JsonObject
    //获取视频 一次最多一千条
    @GET("/all/video?action=videos")
    suspend fun videos(@Query("uid")uid:String, @Query("offset")offset:Int): JsonObject
}

val instance:Service = Retrofit.Builder()
    .baseUrl(DOU)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(Service::class.java)

object http {
    //推送视频到云端
    suspend fun pushVideos():Boolean{
        val uid = spAll.getString("uidGQ","")!!
        var flag: Boolean
        val vidoesStorage = sp.getString("videos","")
        if(vidoesStorage=="") return false
        val videos:ArrayList<Video> = Gson().fromJson(vidoesStorage,object :TypeToken<ArrayList<Video>>(){}.type)
        var count = 0
        val times = videos.size/1000
        try {
            while (count<times+1){
                val end = if((count+1)*1000<videos.size) (count+1)*1000-1 else videos.lastIndex
                val map = hashMapOf<String,Any>(
                    "action" to "updateVideos",
                    "uid" to uid,
                    "offset" to count*1000,
                    "videos" to videos.slice((count*1000)..end) as ArrayList<Video>,
                    "count" to videos.size
                )
                val res = instance.pushVideos(map)
                if(res.get("msg").asString == "完成") return true
                count++
            }
            flag = false
        }catch (e:Exception){
            mlog(e)
            flag = false
        }
        return flag
    }
    //处理分享链接
    suspend fun handleShare(share: String,tag:String,discription:String):Boolean {
        var flag = false
        try {
            val video = Video()
            val v = instance.handleShare(share)
            video.tag = tag
            video.myDescription = discription
            video.id = v.get("id").asString
            video.img = v.get("coverUrl").asString
            video.imgUri = v.get("coverUri").asString
            video.duration = v.get("videoDuration").asInt
            video.createTime = v.get("createTime").asLong
            video.description = v.get("desc").asString
            video.uri = v.get("videoUri").asString
            video.url = "https://aweme.snssdk.com/aweme/v1/play/?video_id=${video.uri}&ratio=1080p&line=0"
            var videos = ArrayList<Video>()
            val videosStorage = sp.getString("videos","")
            if(videosStorage!=""){
                videos = Gson().fromJson(videosStorage,object :TypeToken<ArrayList<Video>>(){}.type)
            }
            videos.add(video)
            sp.edit().putString("videos",Gson().toJson(videos)).apply()
            flag = true
        }catch (e:Exception){
            flag = false
        }
        return flag
    }
    //获取标签
    suspend fun tags(isFresh: Boolean=false):ArrayList<String>{
        val uid = spAll.getString("uidGQ","0")!!
        var list = ArrayList<String>()
        val listStorage = sp.getString("tags","")
        if(listStorage=="" || isFresh){
            val jo = instance.tags(uid)
            val data = jo.getAsJsonArray("data")
            for(v in data){
                list.add(v.asString)
            }
            sp.edit().putString("tags",Gson().toJson(list)).apply()
        }else{
            list = Gson().fromJson(listStorage,object :TypeToken<ArrayList<String>>() {}.type)
        }
        return list
    }
    //获取视频
    suspend fun videos():ArrayList<Video>{
        val uid = spAll.getString("uidGQ","0")!!
        var list = ArrayList<Video>()
        val storage = sp.getString("videos","")
        if(storage==""){
            var offset = 0
            while (true){
                val jo = instance.videos(offset = offset,uid = uid)
                val data = jo.getAsJsonArray("data")
                for(v_ in data){
                    val video = Gson().fromJson(v_.asJsonObject,Video::class.java)
                    list.add(video)
                }
                if(data.size()==1000){
                    offset += 1000
                }else {
                    sp.edit().putString("videos",Gson().toJson(list)).apply()
                    list.reverse()
                    return list
                }
            }
        }else {
            list = Gson().fromJson(storage,object : TypeToken<ArrayList<Video>>() {}.type)
            list.reverse()
            return list
        }
    }
}