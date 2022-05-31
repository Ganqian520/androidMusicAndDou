package com.ganqian.aaa

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.http.SslError
import android.view.View
import android.webkit.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.ganqian.compose.R
import com.ganqian.music.MusicActivity
import com.ganqian.music.viewModel.VmPlayer
import com.ganqian.pianoWindow.pianoWindowActivity
import com.ganqian.video.VideoActivity
import kotlinx.coroutines.launch
//底部弹窗
@Composable
fun PopBottom(
    modifier: Modifier = Modifier,
    isShow: MutableState<Boolean>,
    Content: @Composable () -> Unit
) {
    if(isShow.value) Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = { isShow.value = false })
        }
        .background(Color(0f, 0f, 0f, 0.3f)),
        contentAlignment = Alignment.BottomCenter){
        Box(
            modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
        ) {
            Bg()
            Content()
        }
    }
}
//背景图
@Composable
fun Bg() {
    Image(
        painter = rememberImagePainter(data = R.drawable.bg1,
            builder = {
                transformations(BlurTransformation(MyApplication.appContext, 20f, 20f))
            }),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    )
}
//瀑布流布局
@Composable
fun WaterfallFlowLayout(
    modifier: Modifier = Modifier,
    columns: Int = 2,  // 横向几列
    content: @Composable ()->Unit
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables: List<Measurable>, constrains: Constraints ->
        val itemWidth = constrains.maxWidth / columns
        val itemConstraints = constrains.copy(minWidth = itemWidth, maxWidth = itemWidth)
        val placeables = measurables.map { it.measure(itemConstraints) }
        // 记录当前各列高度
        val heights = IntArray(columns)
        layout(width = constrains.maxWidth, height = constrains.maxHeight){
            placeables.forEach { placeable ->
                val minIndex = heights.minIndex()
                placeable.placeRelative(itemWidth * minIndex, heights[minIndex])
                heights[minIndex] += placeable.height
            }
        }
    }
}

//webview
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(url:String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.domStorageEnabled = true
            webView.settings.mediaPlaybackRequiresUserGesture = false
            webView.webViewClient = WebViewClient()
            webView.loadUrl(url)
            return@AndroidView webView
        }
    )
}
//activity跳转
@Composable
fun Activities(activityStr:String){
    val context = MyApplication.appContext
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .horizontalScroll(rememberScrollState())
        ,horizontalArrangement = Arrangement.SpaceEvenly){
        Box(
            Modifier
                .size(50.dp)
                .clickable {
                    if (activityStr != "music") {
                        val intent = Intent(context, MusicActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        spAll
                            .edit()
                            .putString("activity", "music")
                            .apply()
                    }
                },contentAlignment = Alignment.Center){
            Text("音乐",fontWeight= if(activityStr=="music") FontWeight.W800 else FontWeight.Normal)
        }
        Box(
            Modifier
                .size(50.dp)
                .clickable {
                    if (activityStr != "video") {
                        val intent = Intent(context, VideoActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        spAll
                            .edit()
                            .putString("activity", "video")
                            .apply()
                    }
                },contentAlignment = Alignment.Center){
            Text("短视频",fontWeight= if(activityStr=="video") FontWeight.W800 else FontWeight.Normal)
        }
        Box(
            Modifier
                .size(50.dp)
                .clickable {
                    if (activityStr != "webView") {
                        val intent = Intent(context, pianoWindowActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("from", "threeJs")
                        context.startActivity(intent)
                    }
                },contentAlignment = Alignment.Center){
            Text("threeJs")
        }
        Box(
            Modifier
                .size(50.dp)
                .clickable {
                    if (activityStr != "webView") {
                        val intent = Intent(context, pianoWindowActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("from", "pianoWindow")
                        context.startActivity(intent)
                    }
                },contentAlignment = Alignment.Center){
            Text("钢琴窗")
        }
    }
}
//抽屉
@Composable
fun Drawer(Left: @Composable () -> Unit = {}, Middle: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    val animate = remember { Animatable(1f) }
    val scaleRange = 0.3f //缩放范围0.7~1
    
    val modifierL = Modifier
        .padding(end = ((screen.w * 0.2).p2d).dp)
        .scale(1 - scaleRange + scaleRange * (1 - animate.value))
    val modifierM = Modifier
        .offset(x = (screen.w * 0.8 * (1 - animate.value) - screen.w * scaleRange * (1 - animate.value) / 2).toInt().p2d.dp)
        .scale((1 - scaleRange) + scaleRange * animate.value)
        .pointerInput(Unit) {
            detectDragGestures(
                onDrag = { _, offset ->
                    var temp = animate.value
                    temp -= (offset.x / screen.w * 1.4).toFloat()
                    if (temp < 0) temp = 0f
                    if (temp > 1) temp = 1f
                    scope.launch { animate.snapTo(temp) }
                },
                onDragEnd = {
                    scope.launch {
                        if (animate.value > 0.5) {
                            animate.animateTo(1f, animationSpec = tween(200, easing = LinearEasing))
                        } else {
                            animate.animateTo(0f, animationSpec = tween(200, easing = LinearEasing))
                        }
                    }
                }
            )
            awaitPointerEventScope {
                val event = awaitPointerEvent(PointerEventPass.Initial)
//                event.changes[0].consumeAllChanges()
                drag(event.changes[0].id) {
                    mlog(it.position)
                }
            }
        }
        
    Box(Modifier.fillMaxSize()) {
        Box(modifierL) { if(animate.value!=1f) Left() }
        Box(modifierM.align(Alignment.CenterEnd)) { Middle() }
    }
}
//背景图
@Composable
fun Bg(activity: Activity) {
    Image(
        painter = rememberImagePainter(data = R.drawable.bg1,
            builder = {
                transformations(BlurTransformation(activity, 20f, 20f))
            }),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    )
}
//信号栏
@Composable
fun StatusBar() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(screen.sbH.p2d.dp)
    )
}

//滑动条
@Composable
fun MySlider(
    modifier: Modifier = Modifier,
    thumbH: Dp = 10.dp,
    trackH: Dp = 5.dp,
    color1: Color = Color.White,
    color2: Color = Color(1f, 1f, 1f, 0.3f),
    value: Float = 0.5f,
    onChanging: (current: Float) -> Unit,
    onChanged: (current: Float) -> Unit,
) {
    
    Box(
        modifier
            .fillMaxWidth()
            .height(thumbH)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change: PointerInputChange, offset: Offset ->
                        System.out.println(offset);
                    }
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = thumbH / 2, vertical = (thumbH - trackH) / 2)
        ) {
            Surface(
                Modifier
                    .width(50.dp)
                    .fillMaxHeight(),
                color = color1,
                shape = RoundedCornerShape(trackH / 2)
            ) {
            
            }
            Surface(
                Modifier
                    .width(50.dp)
                    .weight(1f)
                    .fillMaxHeight(),
                color = color2,
                shape = RoundedCornerShape(trackH / 2)
            ) {
            
            }
        }
        Surface(
            modifier = Modifier
                .size(thumbH)
                .offset(50.dp),
            shape = RoundedCornerShape(thumbH / 2),
            color = color1,
        ) {}
    }
}