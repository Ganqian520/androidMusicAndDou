package com.ganqian.video.page.play

import android.util.Log
import android.widget.PopupMenu
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ganqian.aaa.PopBottom
import com.ganqian.aaa.mlog
import com.ganqian.aaa.p2d
import com.ganqian.aaa.screen
import com.ganqian.video.VideoActivity.Companion.context
import com.ganqian.video.Vm
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.gson.Gson

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScreenPlay(index: Int, vm: Vm) {
    val vmPlay: VmPlay = viewModel(factory = VmPlayFactory(vm))
    vmPlay.pagerState = rememberPagerState(initialPage = index)
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(screen.sbH.p2d.dp)
        )
        VerticalPager(
            count = vm.playVideos.size,
            state = vmPlay.pagerState,
        ) {
            Box(Modifier.fillMaxSize()) {
                VideoView(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { vmPlay.isShowState = !vmPlay.isShowState },
                                onDoubleTap = { offset ->
                                    if (vmPlay.isPlay) vmPlay.players[it]?.pause() else vmPlay.players[it]?.play()
                                    vmPlay.isPlay = !vmPlay.isPlay
                                },
                                onLongPress = {
                                    vmPlay.isPopBottom.value = true
                                }
                            )
                        }, vm, vmPlay, it
                )
                if(vmPlay.isShowState) State(Modifier.align(Alignment.BottomCenter),vm,it)
            }
        }
    }
    PopMenu(vmPlay = vmPlay,vm)
    
}

//信息展示
@Composable
fun State(modifier: Modifier,vm:Vm,index: Int) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(15.dp)) {
        Text(vm.playVideos[index].description)
        Row() {
            Text("${vm.playVideos.size-index}")
        }
    }
}

//底部弹窗
@Composable
fun PopMenu(vmPlay: VmPlay,vm:Vm) {
    PopBottom(isShow = vmPlay.isPopBottom) {
        Column(Modifier.fillMaxSize().padding(15.dp)) {
            Row(
                Modifier
                    .clickable { }
                    .height(50.dp)
                    .fillMaxWidth(),verticalAlignment = Alignment.CenterVertically){
                Text("修改标签")
            }
            Box(
                Modifier
                    .height(100.dp)
                    .verticalScroll(rememberScrollState())){
                Text(Gson().toJson(vm.playVideos[vm.indexVideo]))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun VideoView(modifier: Modifier, vm: Vm, vmPlay: VmPlay, index: Int) {
    LaunchedEffect(key1 = Unit, block = {
        if (vmPlay.players[index] == null) vmPlay.players[index] =
            initPlayer(vm.playVideos[index].url)
    })
    DisposableEffect(key1 = Unit) {
        onDispose {
            vmPlay.players[index]?.release()
            vmPlay.players[index] = null
        }
    }
    if (vmPlay.pagerState.currentPage == index) {
//        vmPlay.players[index]?.seekTo(0L)
        vmPlay.players[index]?.play()
    } else {
        vmPlay.players[index]?.pause()
    }
    if (vmPlay.players[index] != null) AndroidView(
        modifier = modifier,
        factory = {
            val view = PlayerView(it)
            view.player = vmPlay.players[index]
            view.useController = false
            return@AndroidView view
        })
}

fun initPlayer(url: String): ExoPlayer {
    val player = ExoPlayer.Builder(context).build()
    val mediaItem = MediaItem.fromUri(url)
    val dataSource = DefaultDataSource(context, true)
    val factory = DataSource.Factory { dataSource }
    val mediaSource = ProgressiveMediaSource
        .Factory(factory)
        .createMediaSource(mediaItem)
    player.setMediaSource(mediaSource)
    player.repeatMode = Player.REPEAT_MODE_ONE
    player.prepare()
    return player
}