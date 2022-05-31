package com.ganqian.music.component

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import coil.transform.RoundedCornersTransformation
import com.ganqian.aaa.*
import com.ganqian.compose.R
import com.ganqian.music.MusicActivity
import com.ganqian.music.page.bang.PageTopList
import com.ganqian.music.page.comment.PageComment
import com.ganqian.music.page.geDanDetail.PageGeDanDetail
import com.ganqian.music.page.geDanSquare.PageGeDanSquare
import com.ganqian.music.page.index.PageIndex
import com.ganqian.music.page.search.PageSearch
import com.ganqian.music.page.play.PagePlay
import com.ganqian.music.ui.Theme_
import com.ganqian.music.ui.aliFontFamily
import com.ganqian.music.ui.iconStyle
import com.ganqian.music.view.SongList
import com.ganqian.music.viewModel.VmLogin
import com.ganqian.music.viewModel.VmPlayer

@Composable
fun App(activity: Activity, vm: VmPlayer, vmLogin: VmLogin) {
    vm.navController = rememberNavController()
    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            vm.saveCache()
        }
    })
    Theme_ {
        Box(modifier = Modifier.fillMaxSize()) {
            Bg(vm = vm, activity = activity)
            StatusBar()
            NavHost(
                navController = vm.navController,
                startDestination = "index",
            ) {
                composable("index", content = { PageIndex(vm = vm, vmLogin = vmLogin) })
                composable("search", content = { PageSearch(vm) })
                composable("gedanSquare",content = { PageGeDanSquare(vm)})
                composable("topList",content = { PageTopList(vmPlayer = vm)})
                composable(
                    "geDanDetail/{from}",
                    arguments = listOf(navArgument("from", {})),
                    content = {
                        PageGeDanDetail(it.arguments!!.getString("from")!!, vm)
                })
                composable(
                    route = "comment/{id}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.StringType }
                    ),
                    content = {
                        val id = it.arguments!!.getString("id")
                        PageComment(id!!)
                    }
                )
            }
            PopSongList(vm)
            PopDouMore(vm)
            if (vm.isShowPlay) PagePlay(vm = vm)
        }
    }
}
//抖音歌曲列表项更多弹窗
@Composable
fun PopDouMore(vm: VmPlayer){
    PopBottom(isShow = vm.isOpenMore, vm = vm) {
        Column(Modifier.padding(vertical = 10.dp)) {
            Row(Modifier.height(50.dp).fillMaxWidth()
                .clickable { vm.deleteDouMusic() }
                .padding(horizontal = 10.dp)
                ,verticalAlignment = Alignment.CenterVertically) {
                Text("删除")
            }
        }
    }
}
//弹出歌曲列表，显示插队列表和播放来源
@Composable
fun PopSongList(vm: VmPlayer) {
    PopBottom(vm = vm, isShow = vm.isShowWant) {
        Column {
            Text(
                text = "优先队列",
                modifier = Modifier.padding(start = 10.dp, top = 20.dp, bottom = 20.dp)
            )
            SongList(vm = vm, list = vm.listWant, mode = "other")
        }
    }
    
}

//公共底部弹窗容器
@Composable
fun PopBottom(
    modifier: Modifier = Modifier,
    isShow: MutableState<Boolean>,
    vm: VmPlayer,
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
            Bg(vm)
            Content()
        }
    }
}

//背景图
@Composable
fun Bg(vm: VmPlayer, activity: Activity = MusicActivity.context as Activity) {
    Image(
        painter = rememberImagePainter(
            data = R.drawable.bg1,
            builder = {
                transformations(BlurTransformation(activity, 20f, 20f))
                crossfade(true)
            },
        ),
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.Center,
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
    Image(
        painter = rememberImagePainter(
            data = vm.song.img,
            builder = {
                transformations(BlurTransformation(activity, 20f, 20f))
                crossfade(true)
            },
        ),
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.Center,
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(color = Color(0f, 0f, 0f, 0.1f))
    )
}

//底部控件 大多数页面都有
@Composable
fun Control(vm: VmPlayer) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { vm.isShowPlay = true }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0f, 0f, 0f, 0.2f), Color.Transparent),
                    startY = 0f,
                    endY = aautil.sizeTo(70, "d2p")
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val angle = rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        if (vm.isPlay) {
            val noUse = angle.value //用来触发更新
            vm.angle += 0.3f
            if (vm.angle > 360) vm.angle = 0f
        }
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = rememberImagePainter(
                data = vm.song.img,
                builder = {
                    transformations(RoundedCornersTransformation(aautil.sizeTo(35, "d2p")))
                },
            ),
            modifier = Modifier
                .size(70.dp)
                .rotate(vm.angle),
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            Modifier.weight(1f)
        ) {
            Text(
                text = vm.song.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = vm.song.author,
                fontSize = 13.sp,
                color = Color(1f, 1f, 1f, 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (vm.isPlay) {
            Text(
                text = stringResource(id = R.string.pause),
                style = iconStyle,
                fontSize = 30.sp,
                modifier = Modifier
                    .width(50.dp)
                    .clickable {
                        vm.pause()
                    },
            )
        } else {
            Text(
                text = stringResource(id = R.string.play),
                style = iconStyle,
                fontSize = 25.sp,
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
            fontSize = 20.sp,
            modifier = Modifier
                .width(40.dp)
                .clickable {
                    vm.next()
                },
        )
        Text(
            text = stringResource(id = R.string.list),
            style = iconStyle,
            fontSize = 20.sp,
            modifier = Modifier
                .width(40.dp)
                .clickable { vm.isShowWant.value = !vm.isShowWant.value }
        )
    }
}

//小弹窗的背景
@Composable
fun PopBg(modifier: Modifier = Modifier, vm: VmPlayer, compose: @Composable () -> Unit) {
    Box(
        modifier,
    ) {
        Bg(vm = vm)
        compose()
    }
}
