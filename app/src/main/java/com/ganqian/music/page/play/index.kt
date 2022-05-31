package com.ganqian.music.page.play

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.audiofx.Visualizer
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.ganqian.aaa.StatusBar
import com.ganqian.aaa.aautil
import com.ganqian.aaa.advancedShadow
import com.ganqian.aaa.screen
import com.ganqian.compose.R
import com.ganqian.music.MusicActivity
import com.ganqian.music.api.http
import com.ganqian.music.component.Bg
import com.ganqian.music.component.PopBg
import com.ganqian.music.data.Lyric
import com.ganqian.music.page.play.paint
import com.ganqian.music.ui.aliFontFamily
import com.ganqian.music.ui.iconStyle
import com.ganqian.music.util.util
import com.ganqian.music.viewModel.VmPlayer
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.util.*


@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("UnrememberedAnimatable")
@Composable
fun PagePlay(vm: VmPlayer, activity: Activity = MusicActivity.context as Activity) {
    val h = screen.allH
    val w = screen.w
    val isShowMenu = remember { mutableStateOf(false) }
    val alphaAni = remember {
        Animatable(0f)
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit, block = {
        alphaAni.animateTo(
            1f,
            animationSpec = tween(durationMillis = 300)
        )
    })
    DisposableEffect(key1 = Unit, effect = {
        onDispose {
        
        }
    })
    fun startAni() {
        if (alphaAni.value < 0.8) {
            scope.launch {
                alphaAni.animateTo(0f) {
                    if (this.value == 0f) {
                        vm.isShowPlay = false
                    }
                }
            }
        } else {
            scope.launch {
                alphaAni.animateTo(1f)
            }
        }
    }
    vm.backPlay = {
        scope.launch {
            alphaAni.animateTo(0f, animationSpec = tween(durationMillis = 300)) {
                if (this.value == 0f) {
                    vm.isShowPlay = false
                }
            }
        }
    }
    Box(
        Modifier
            .graphicsLayer(
                translationX = -w * (1 - alphaAni.value),
                translationY = h * (1 - alphaAni.value),
                alpha = alphaAni.value,
                scaleX = alphaAni.value,
                scaleY = alphaAni.value,
                clip = true,
            )
            .clip(RoundedCornerShape(20.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, offset ->
                        var alpha = alphaAni.value
                        alpha -= offset.y / h
                        if (alpha > 1) alpha = 1f
                        scope.launch { alphaAni.snapTo(alpha) }
                    },
                    onDragEnd = {
                        startAni()
                    }
                )
            }
    ) {
        Bg(vm = vm, activity = activity)
        Column(
            modifier = Modifier
        ) {
            StatusBar()
            TopBar(vm = vm, isShowMenu)
            Column(Modifier.weight(1f)) {
                Middle(vm = vm)
            }
            Bottom(vm = vm)
        }
        PopMenu(vm = vm, isShowMenu = isShowMenu)
    }
    
}

//顶部栏
@Composable
fun TopBar(vm: VmPlayer, isShowMenu: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height((60.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.xia),
            fontFamily = aliFontFamily,
            modifier = Modifier
                .width(50.dp)
                .clickable { },
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = vm.song.name,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = vm.song.author,
                fontSize = 13.sp,
                color = Color(1f, 1f, 1f, 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = stringResource(id = R.string.more),
            fontFamily = aliFontFamily,
            modifier = Modifier
                .width(50.dp)
                .clickable { isShowMenu.value = !isShowMenu.value },
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )
    }
}

//顶部栏控制的菜单
@Composable
fun PopMenu(vm: VmPlayer, isShowMenu: MutableState<Boolean>) {
    AnimatedVisibility(
        modifier = Modifier.offset(y = 100.dp),
        visible = isShowMenu.value,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut(),
    ) {
        PopBg(
            vm = vm, modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(id = R.string.unlike),
                    style = iconStyle,
                    modifier = Modifier.clickable { }
                )
                Text(
                    text = stringResource(id = R.string.like),
                    style = iconStyle,
                    modifier = Modifier.clickable {
                        GlobalScope.launch {
                            val is200 = http.addLike(vm.song.id)
                            aautil.toast(is200, "收藏成功", "收藏失败")
                        }
                    }
                )
                Text(
                    text = stringResource(id = R.string.comment),
                    style = iconStyle,
                    modifier = Modifier.clickable {
                        if (vm.song.platform == "net") {
                            vm.navController.navigate("comment/${vm.song.id}")
                            vm.isShowPlay = false
                        }
                    }
                )
            }
        }
    }
    
    
}

//中间
@Composable
fun Middle(vm: VmPlayer) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Lyric(vm)
        Image(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .advancedShadow(),
            painter = rememberImagePainter(
                data = vm.song.img,
                builder = { },
            ),
            contentDescription = null
        )
        Fft(vm = vm)
    }
}

//歌词
@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Lyric(vm: VmPlayer) {
    if (vm.song.platform != "net") return
    var list by remember { mutableStateOf(ArrayList<Lyric>()) }
    var isByUser by remember { mutableStateOf(false) }
    val initY = util.sizeTo(75)    //canvas高度的一半
    var y by remember { mutableStateOf(initY) } //绘制用的y
    var isByAnimator by remember { mutableStateOf(false) } //动画中就不计算
    val column = 5  //最大歌词数
    val timer = Timer() //手指离开三秒后才能动
    var task: TimerTask? = null
    var isLoading by remember { mutableStateOf(true)} //加载歌词中
    fun scroll(newY: Float, duration: Long, isByuser_: Boolean = false) {
        if (y != newY) {
            isByAnimator = true
            val animator = ValueAnimator.ofFloat(y, newY)
            animator.addUpdateListener {
                y = it.animatedValue as Float
                if ((it.animatedValue as Float) == newY) {
                    isByAnimator = false
                    isByUser = isByuser_
                }
            }
            animator.duration = duration
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    animator.start()
                }
            }
        }
    }
    LaunchedEffect(vm.song) {
        isLoading = true
        list = if(vm.mode=="net" || vm.mode=="dou") http.getLyric(vm.song.id) else http.getLyric(vm.song.id)
        isLoading = false
        vm.cbLyric = {
            if (!isByUser and !isByAnimator) {
                val newY = initY - util.findMiddle(list, it) * (initY * 2 / column)
                scroll(newY, 300)
            }
        }
    }
    if(!isLoading){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(150.dp)
                .pointerInput(Unit) {
                    if (isByAnimator) return@pointerInput
                    detectDragGestures(
                        onDragStart = {
                            isByUser = true
                        },
                        onDrag = { change: PointerInputChange, offset: Offset ->
                            y += offset.y
                            val min = initY - (list.size - 1) * (initY * 2 / column)
                            if (y < min) y = min
                            if (y > initY) y = initY
                        },
                        onDragEnd = {
                            val h = initY * 2 / column
                            val quotient = ((initY - y) / h).toInt()
                            val remainder = (initY - y) % h
                            var newY = 0f
                            if (remainder < h / 2) {
                                newY = initY - quotient * h
                            } else {
                                newY = initY - (quotient + 1) * h
                            }
                            scroll(newY, 300, true)
                            task?.cancel()
                            task = object : TimerTask() {
                                override fun run() {
                                    val newY_ = initY - util.findMiddle(
                                        list,
                                        vm.mediaPlayer.currentPosition
                                    ) * (initY * 2 / column)
                                    scroll(newY_, 500)
                                }
                            }
                            timer.schedule(task, 3000)
                        }
                    )
                }
        ) {
            Canvas(
                Modifier.fillMaxSize()
            ) {
                val w = size.width
                val h = size.height
                drawIntoCanvas {
                    for ((i, v) in list.withIndex()) {
                        util.myDrawText(v.content, w / 2, y + i * (h / column), it.nativeCanvas, paint)
                    }
                }
            }
        
            if (isByUser) Row(
                Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val indexMiddle = ((initY - y) / (2 * initY / column)).toInt()
                Text(
                    modifier = Modifier
                        .width(40.dp)
                        .clickable { vm.seekTo(list[indexMiddle].millisecond) },
                    text = stringResource(id = R.string.play),
                    color = Color(1f, 1f, 1f, 0.5f),
                    textAlign = TextAlign.Center,
                    fontFamily = aliFontFamily
                )
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color = Color(1f, 1f, 1f, 0.2f))
                )
                Text(
                    modifier = Modifier.width(50.dp),
                    text = list[indexMiddle].format,
                    color = Color(1f, 1f, 1f, 0.5f),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
            }
        }
    }else{
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(150.dp)
        ){
            Text("加载歌词中...",
            fontSize = 14.sp,
            color = Color(1f,1f,1f,0.7f))
        }
    }
}

//频谱
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Fft(vm: VmPlayer) {
    var data by remember { mutableStateOf(IntArray(64)) }
    var visualizer: Visualizer? = null
    LaunchedEffect(key1 = Unit, block = {
        visualizer = Visualizer(vm.mediaPlayer.audioSessionId)
        visualizer?.enabled = false
        visualizer?.captureSize = 128
        visualizer?.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) {}
            
            override fun onFftDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) {
                val fft: ByteArray = p1!!
                val magnitudes = IntArray(fft.size / 2)
                var max = 0
                for (i in magnitudes.indices) {
                    magnitudes[i] = Math.hypot(
                        fft[2 * i].toDouble(),
                        fft[2 * i + 1].toDouble()
                    ).toInt()
                    if (magnitudes[max] < magnitudes[i]) {
                        max = i
                    }
                }
                data = magnitudes
            }
        }, Visualizer.getMaxCaptureRate() / 2, false, true)
        visualizer?.enabled = true
    })
    DisposableEffect(key1 = Unit) {
        onDispose {
            visualizer?.enabled = false
            visualizer?.release()
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 20.dp)
    ) {
        Canvas(
            Modifier.fillMaxSize()
        ) {
            if (data.sum() == 0) return@Canvas
            val w = size.width
            val h = size.height
            val path = Path()
            val color = Color(1f, 1f, 1f, 0.7f)
            path.moveTo(0f, h / 2)
            for ((i, v) in data.withIndex()) {
                path.lineTo(w / (data.size + 1) * (i + 1), h / 2 - v.toFloat())
            }
            path.lineTo(w, h / 2)
            for (i in data.indices) {
                path.lineTo(
                    w - w / (data.size + 1) * (i + 1),
                    h / 2 + data[data.size - 1 - i].toFloat()
                )
            }
            path.lineTo(0f, h / 2)
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 1f)
            )
            for ((i, v) in data.withIndex()) {
                drawLine(
                    start = Offset(w / (data.size + 1) * (i + 1), h / 2 - v),
                    end = Offset(w / (data.size + 1) * (i + 1), h / 2 + v),
                    color = color,
                    strokeWidth = 1f
                )
            }
        }
    }
}

//底部
@Composable
fun Bottom(vm: VmPlayer) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Progress(vm)
        Control(vm = vm)
    }
}

//进度条
@Composable
fun Progress(vm: VmPlayer) {
    var progress by remember { mutableStateOf(0f) }
    var isByUser by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        vm.cbProgress = {
            if (!isByUser) progress = (it / 1000).toFloat() / vm.song.duration
        }
    }
    Row(
        modifier = Modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = util.transTime((vm.song.duration * progress).toInt()),
            Modifier.width(50.dp),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
        )
        Slider(
            modifier = Modifier
                .weight(1f),
            value = progress,
            onValueChange = {
                progress = it
                isByUser = true
            },
            onValueChangeFinished = {
                vm.seekTo((vm.song.duration * 1000 * progress).toInt())
                isByUser = false
            },
            colors = SliderDefaults.colors(
                inactiveTrackColor = Color(1f, 1f, 1f, 0.1f),
                activeTrackColor = Color(1f, 1f, 1f, 0.2f),
                thumbColor = Color(1f, 1f, 1f, 0.2f)
            )
        )
        Text(
            text = util.transTime(vm.song.duration),
            Modifier.width(50.dp),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
        )
    }
}

//播放控制
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Control(vm: VmPlayer) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (vm.mode != "fm") {
            val fontSize: TextUnit
            val order = when (vm.order) {
                0 -> {
                    fontSize = 14.sp
                    stringResource(id = R.string.shunxv)
                }
                1 -> {
                    fontSize = 18.sp
                    stringResource(id = R.string.suiji)
                }
                else -> {
                    fontSize = 18.sp
                    stringResource(id = R.string.danqu)
                }
            }
            
            Text(
                text = order,
                fontFamily = aliFontFamily,
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                modifier = Modifier
                    .width(30.dp)
                    .clickable {
                        vm.order = if (vm.order == 2) 0 else vm.order + 1
                    },
            )
            Text(
                text = stringResource(id = R.string.last),
                fontFamily = aliFontFamily,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier
                    .width(30.dp)
                    .clickable { vm.last() },
            )
        } else {
            Text(
                text = stringResource(id = R.string.unlike),
                style = iconStyle,
                fontSize = 18.sp,
                modifier = Modifier
                    .width(30.dp)
                    .clickable {
                        GlobalScope.launch {
            
                        }
                    },
            )
            Text(
                text = stringResource(id = R.string.like),
                style = iconStyle,
                fontSize = 18.sp,
                modifier = Modifier
                    .width(30.dp)
                    .clickable {
                        GlobalScope.launch {
                            val is200 = http.addLike(vm.song.id)
                            aautil.toast(is200, "收藏成功", "收藏失败")
                        }
                    },
            )
        }
        if (vm.isPlay) {
            Text(
                text = stringResource(id = R.string.pause),
                fontFamily = aliFontFamily,
                textAlign = TextAlign.Center,
                fontSize = 40.sp,
                modifier = Modifier
                    .width(50.dp)
                    .clickable {
                        vm.pause()
                    },
            )
        } else {
            Text(
                text = stringResource(id = R.string.play),
                fontFamily = aliFontFamily,
                textAlign = TextAlign.Center,
                fontSize = 35.sp,
                modifier = Modifier
                    .width(50.dp)
                    .clickable {
                        vm.resume()
                    },
            )
        }
        Text(
            text = stringResource(id = R.string.next),
            fontFamily = aliFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            modifier = Modifier
                .width(30.dp)
                .clickable {
                    vm.next()
                },
        )
        Text(
            text = stringResource(id = R.string.list),
            fontFamily = aliFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            modifier = Modifier
                .width(30.dp)
                .clickable { vm.isShowWant.value = !vm.isShowWant.value },
        )
    }
}

internal val gradient = LinearGradient(
    0f, 0f, 0f, util.sizeTo(150),
    intArrayOf(
        android.graphics.Color.TRANSPARENT,
        android.graphics.Color.WHITE,
        android.graphics.Color.TRANSPARENT
    ),
    floatArrayOf(0f, 0.5f, 1f),
    Shader.TileMode.CLAMP
)
internal val paint = Paint().asFrameworkPaint().apply {
    isAntiAlias = true
    textSize = 40F
    shader = gradient
}