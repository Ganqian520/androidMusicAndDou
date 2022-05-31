package com.ganqian.music.page.floatingWindow

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.ganqian.aaa.d2p
import com.ganqian.aaa.mlog
import com.ganqian.aaa.screen
import com.ganqian.compose.R
import com.ganqian.music.util.sp
import com.ganqian.video.VideoActivity
import com.ganqian.video.Vm

class ServiceListenMusic : LifecycleService(), SavedStateRegistryOwner {

    val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    lateinit var composeView: ComposeView
    lateinit var textView: TextView
    val layoutParams1 = WindowManager.LayoutParams()
    val layoutParams2 = WindowManager.LayoutParams()
    lateinit var control: Control
    lateinit var clipboardManager: ClipboardManager
    lateinit var clipChangedListener: ClipboardManager.OnPrimaryClipChangedListener
    val savedStateRegistryController = SavedStateRegistryController.create(this)

    var share = "" //分享链接

    override fun getSavedStateRegistry(): SavedStateRegistry {
        return savedStateRegistryController.savedStateRegistry
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return control
    }

    override fun onCreate() {
        super.onCreate()
        composeView = ComposeView(baseContext).apply {
            setContent { ListenMusic(control) }
        }
        
        ViewTreeLifecycleOwner.set(composeView, this)
        ViewTreeSavedStateRegistryOwner.set(composeView, this)
        savedStateRegistryController.performRestore(null)
        
        initClipboard()
        initLayoutParams()
        control = Control()
        textView = TextView(baseContext)
        textView.text = "占位"
        textView.textSize = 500f
        textView.setBackgroundColor(R.drawable.bg1)
        windowManager.addView(textView,layoutParams2)
//        windowManager.addView(composeView,layoutParams1)
    }

    fun initLayoutParams(){
        layoutParams1.format = PixelFormat.TRANSPARENT
        layoutParams1.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        layoutParams1.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams1.width = screen.w - 15.d2p
        layoutParams1.height = screen.h / 4
        layoutParams1.alpha = 1f

        layoutParams2.format = PixelFormat.TRANSPARENT
        layoutParams2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        layoutParams2.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams2.width = 1
        layoutParams2.height = 1
        layoutParams2.gravity = Gravity.TOP
    }

    fun initClipboard(){
        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
            if (clipboardManager.hasPrimaryClip() && clipboardManager.primaryClip?.itemCount!! > 0) {
                share = clipboardManager.primaryClip!!.getItemAt(0).text as String
                sp.edit().putString("share",share).apply()
                windowManager.addView(composeView, layoutParams1)
            }
        }
        clipboardManager.addPrimaryClipChangedListener(clipChangedListener)
    }

    inner class Control :Binder(){
        fun closeAll(){
            try {
                windowManager.removeView(composeView)
            }catch (e:Exception){System.out.println("可能还没添加：$e")}
            windowManager.removeView(textView)
        }
        fun openWindow(){
            windowManager.addView(composeView, layoutParams1)
        }
        fun closeWindow(){
            windowManager.removeView(composeView)
        }
    }

}
/*
* FLAG_NOT_TOUCH_MODAL 即使在该window在可获得焦点情况下，仍然把该window之外的任何event发送到该window之后的其他window
* FLAG_NOT_FOCUSABLE 让window不能获得焦点，这样用户快就不能向该window发送按键事件及按钮事件
* */
