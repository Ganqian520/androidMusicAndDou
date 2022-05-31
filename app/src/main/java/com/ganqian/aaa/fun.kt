package com.ganqian.aaa

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

//微信登录
fun loginWwchat(){

}
//qq登录
fun loginQQ(context: Context){
    val tencent = Tencent.createInstance("101992888",context)
    val listener = object : IUiListener {
        override fun onComplete(p0: Any?) {
            mlog("complete");
        }
        override fun onCancel() {
            mlog("cancel");
        }
        override fun onWarning(p0: Int) {
            mlog("warning");
        }
        override fun onError(p0: UiError?) {
            mlog("error");
        }
    }
    tencent.login(context as Activity,"get_simple_userinfo",listener)
//            val unionInfo = UnionInfo(context,tencent.qqToken)
//            unionInfo.getUnionId(listener)
}

//我的打印
fun mlog(vararg p:Any){
    System.out.println("gq      ==============================");
    for(v in p){
        System.out.println("gq      $v");
    }
}

//弹窗
fun toast(isShow:Boolean = true,yes:String="成功",no:String = "失败"){
    GlobalScope.launch {
        withContext(Dispatchers.Main){
            if(isShow){
                Toast.makeText(MyApplication.appContext,yes, Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(MyApplication.appContext,no, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
}

//授权
fun getPermisson(activity: Activity) {
    val all = arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.RECORD_AUDIO)
    val no = ArrayList<String>()
    for (i in all.indices) {
        val permission = ContextCompat.checkSelfPermission(activity.application, all[i])
        if (permission != PackageManager.PERMISSION_GRANTED) {
            no.add(all[i])
        }
    }
    ActivityCompat.requestPermissions( activity,no.toTypedArray(), 0)
    if (!Settings.canDrawOverlays(activity)) {
//      startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName())), 0);
    }
}
//透明化信号栏
fun tranStatusBar(activity: Activity) {
    val window = activity.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = Color.TRANSPARENT
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}
//重启app
fun reStartAPP(context: Context){
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    context.startActivity(intent)
}

//传入时间戳，返回年月日
@SuppressLint("SimpleDateFormat")
fun getDate(timeStamp:Long):Map<String,Int>{
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val str = format.format(timeStamp)
    val map = hashMapOf<String,Int>(
        "year" to str.substring(0,4).toInt(),
        "month" to str.substring(5,7).toInt(),
        "day" to  str.substring(8,10).toInt()
    )
    return map
}
//传入秒数,返回时分秒，或者浮点数小时
fun tranSecond(total:Int,flag:Int):String{
    if(flag==0){
        val h:Int = total/3600
        val m = total%3600/60
        val s = total-3600*h-60*m
        if(h==0&&m==0) return "${s}秒"
        if(h==0&&m!=0) return "${m}分${s}秒"
        return "${h}时${m}分${s}秒"
    }else {
        val h:String = String.format("%.1f",total.toFloat()/3600)
        return  "${h}h"
    }
}