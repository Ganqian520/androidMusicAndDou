package com.ganqian.music.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ganqian.aaa.UserGQ
import com.ganqian.aaa.mlog
import com.ganqian.aaa.spAll
import com.ganqian.music.api.api
import com.ganqian.music.api.dou
import com.ganqian.music.api.httpDou
import com.ganqian.music.api.uidNet
import com.ganqian.music.data.UserNet
import com.ganqian.music.util.sp
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
@OptIn(DelicateCoroutinesApi::class)
class VmLogin:ViewModel(){
    var user by mutableStateOf(UserNet())
    var count by mutableStateOf(0)
    var phoneNet by mutableStateOf("")
    var phoneGQ by mutableStateOf("")
    var passwordNet by mutableStateOf("")
    var passwordGQ by mutableStateOf("")
    var stateLoginGQ by mutableStateOf(0) //0未登录，1登录中，2登录失败,3登录成功
    var stateLoginNet by mutableStateOf(0)
    //退出登录
    fun quit(platform:String){
        if(platform=="net"){
            stateLoginNet = 0
        }
        if(platform=="dou"){
            stateLoginGQ = 0
        }
    }
    //本系统登录
    fun loginGQ(){
        if(passwordGQ=="" && phoneGQ=="" && stateLoginGQ==1) return
        stateLoginGQ = 1
        GlobalScope.launch {
            val msg = httpDou.loginGQ(phoneGQ,passwordGQ)
            if(msg=="登录成功") {
                stateLoginGQ = 3
                spAll.edit().putInt("stateLoginGQ",3).apply()
                spAll.edit().putString("uidGQ",phoneGQ).apply()
            }
            if(msg=="登录失败") stateLoginGQ = 2
        }
    }
    //网易云登录
    fun loginNet(){
        if(phoneNet=="" && passwordNet=="" && stateLoginNet==1) return
        stateLoginNet = 1
        GlobalScope.launch {
            try {
                val obj = api.instance.loginPassword(phoneNet,passwordNet)
                val id = obj.getAsJsonObject("account").get("id").asString
                val avatarUrl = obj.getAsJsonObject("profile").get("avatarUrl").asString
                val nickName = obj.getAsJsonObject("profile").get("nickname").asString
                user = UserNet(nickName,id,avatarUrl)
                spAll.edit().putString("userNet",Gson().toJson(user)).apply()
                spAll.edit().putInt("stateLoginNet",3).apply()
                stateLoginNet = 3
            }catch (e:Exception){
                mlog("捕获到异常：",e)
                stateLoginNet = 2
            }
        }
    }
    //获取缓存
    fun getCache(){
        val userNet_ = spAll.getString("userNet","")
        if(userNet_!=""){
            val userNet = Gson().fromJson(userNet_,UserNet::class.java)
            user = userNet
            uidNet = userNet.id
        }
        phoneGQ = spAll.getString("uidGQ","")!!
        stateLoginGQ = spAll.getInt("stateLoginGQ",0)
        stateLoginNet = spAll.getInt("stateLoginNet",0)
    }
}
