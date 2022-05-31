package com.ganqian.pianoWindow

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo.*
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ganqian.aaa.WebView
import com.ganqian.aaa.aautil
import com.ganqian.aaa.mlog
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment

class pianoWindowActivity : ComponentActivity() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val from = intent.getStringExtra("from")
        val url = when (from) {
            "threeJs" -> "gq-music-webview.vercel.app"
            "pianoWindow" -> "https://static-ca448d14-fda5-4d8f-9279-3f4896d8f854.bspapp.com/pianowindow/index.html"
            else -> ""
        }
        requestedOrientation = SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        context = this
//        window.attributes = window.attributes.apply {
//            layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//        }
        setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Red)
            ) {
                WebView(url)
            }
        }
    }
}
