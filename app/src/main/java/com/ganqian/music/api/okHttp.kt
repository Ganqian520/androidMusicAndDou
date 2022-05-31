package com.ganqian.music.api

import android.content.Context
import com.ganqian.aaa.mlog
import com.ganqian.music.util.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.File

object OkHttp {
    lateinit var clientC: OkHttpClient
    lateinit var client: OkHttpClient
    fun init(context: Context){
        clientC = OkHttpClient.Builder()
            .addNetworkInterceptor(CacheInterceptor())
            .cookieJar(CookieJar_())
            .cache(Cache(File(context.getExternalCacheDir().toString(),"cache"),(500*1024*1024).toLong()))//500m
            .build()
        client = OkHttpClient.Builder()
            .cookieJar(CookieJar_())
            .build()
    }
}

class  CacheInterceptor :Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)
        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", "max-age=" + 3600 * 24 * 365) //直接存一年
            .build()
    }
}

class CookieJar_ :CookieJar {
    private lateinit var cookie_:String
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        cookie_ = sp.getString("cookie","")!!
        val cookies = if(cookie_!="")
            Gson().fromJson(cookie_, object : TypeToken<ArrayList<Cookie>>() {}.type)
        else ArrayList<Cookie>()
        return cookies
    }
    
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if(cookie_=="") sp.edit().putString("cookie",Gson().toJson(cookies)).apply()
    }
}

