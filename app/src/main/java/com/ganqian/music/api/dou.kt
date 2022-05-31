package com.ganqian.music.api

import com.ganqian.aaa.mlog
import com.ganqian.aaa.spAll
import com.ganqian.music.data.Song
import com.ganqian.music.util.sp
import com.ganqian.video.util.Video
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

interface ServiceDou {
    //登录本系统
    @GET("/all/login")
    suspend fun loginGQ(@Query("uid")uid:String,@Query("password")password:String):JsonObject
    //处理音乐链接
    @GET("all/handleShare")
    suspend fun handleShareMusic(@Query("share")share:String):JsonObject
    //修改音乐
    @POST("/all/music")
    suspend fun updateMusics(@Body map: HashMap<String, Any>):JsonObject
    //获取音乐
    @GET("/all/music?action=getMusics")
    suspend fun getMusics(@Query("uid")uid: String,@Query("offset")offset:Int):JsonObject
    //获取管理员的音乐
    @POST("/index")
    suspend fun getAdmin(@Body map:HashMap<String,String> ): JsonObject
}
object httpDou {
    //登录本系统
    suspend fun loginGQ(p1:String,p2:String):String{
        val res = dou.instance.loginGQ(p1,p2)
        return res.get("msg").asString
    }
    //处理音乐链接
    suspend fun handleShareMusic(share: String,name:String,isVoice:Boolean):Boolean{
        var flag:Boolean
        var musics = ArrayList<Song>()
        try {
            val song = Song()
            val v = dou.instance.handleShareMusic(share)
            song.name = name
            song.tag = if(isVoice) 2 else 1
            song.url = v.get("musicUrl").asString
            song.img = v.get("coverUrl").asString
            song.author = v.get("musicAuthor").asString
            song.duration = v.get("musicDuration").asInt
            song.id = v.get("id").asString
            song.platform = "dou"
            val list_ = sp.getString("musics","")
            if(list_!="") musics = Gson().fromJson(list_,object :TypeToken<ArrayList<Song>>(){}.type)
            musics.add(song)
            sp.edit().putString("musics",Gson().toJson(musics)).apply()
            flag = true
        }catch (e:java.lang.Exception){
            mlog("异常",e)
            flag = false
        }
        return flag
    }
    //修改音乐
    suspend fun updateMusics():Boolean{
        var isOK:Boolean
        val musics_ = sp.getString("musics","")
        val uidGQ = spAll.getString("uidGQ","")!!
        if(musics_=="") return true
        val list:ArrayList<Song> = Gson().fromJson(musics_,object :TypeToken<ArrayList<Song>>(){}.type)
        var count = 0
        val times = if(list.size%1000==0) list.size/1000 else list.size/1000+1
        try {
            while (count<times){
                val end = if((count+1)*1000<list.size) (count+1)*1000-1 else list.lastIndex
                val map = hashMapOf<String,Any>(
                    "uid" to uidGQ,
                    "offset" to count*1000,
                    "musics" to list.slice((count*1000)..end) as ArrayList<Song>,
                    "action" to "updateMusics"
                )
                dou.instance.updateMusics(map)
                count++
            }
            isOK = true
        }catch (e:Exception){
            isOK = false
        }
        return isOK
    }
    //获取音乐
    suspend fun getMusics(isAdmin:Boolean=false):ArrayList<Song>{
        var list = ArrayList<Song>()
//        sp.edit().putString("musics","").apply()
        val musics_ = sp.getString("musics","")!!
        var uidGQ = spAll.getString("uidGQ","")!!
        if(isAdmin) uidGQ = "1"
        if(musics_!=""){
            list = Gson().fromJson(musics_,object :TypeToken<ArrayList<Song>>(){}.type)
            list.reverse()
            return list
        }else{
            var offset = 0
            while (true){
                val obj = dou.instance.getMusics(uidGQ,offset)
                val data = obj.getAsJsonArray("data")
                for(v in data){
                    list.add(Gson().fromJson(v.asJsonObject,Song::class.java))
                }
                if(data.size()%1000==0){  //粗略判断，等于1000说明还有剩余
                    offset += 1000
                }else{
                    sp.edit().putString("musics",Gson().toJson(list)).apply()
                    list.reverse()
                    return list
                }
            }
        }
    }
}
object dou {
    lateinit var instance:ServiceDou
    fun init(){
        val retrofit = Retrofit.Builder()
            .baseUrl(DOU)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        instance = retrofit.create(ServiceDou::class.java)
    }
}