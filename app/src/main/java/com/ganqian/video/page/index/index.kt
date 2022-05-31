package com.ganqian.video.page.index

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.ganqian.aaa.*
import com.ganqian.compose.R
import com.ganqian.video.VideoActivity
import com.ganqian.video.Vm
import com.ganqian.video.aliFontFamily
import com.ganqian.video.api.http
import com.ganqian.video.util.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScreenIndex(vm: Vm) {
    vm.pagerState = rememberPagerState()
    LaunchedEffect(key1 = Unit, block = {
        vm.allVideos = http.videos()
        vm.tags = http.tags()
        vm.tags.add(0, "全部")
        vm.tags.add("待定")
        vm.playVideos = vm.allVideos
//        vm.navController.navigate("play/1")
    })
    Drawer(Left = {
        VideoDrawer(vm = vm)
    },Middle = {
        Box(Modifier.clip(RoundedCornerShape(20.dp))){
            Bg()
            Column(Modifier.fillMaxSize()) {
                StatusBar()
                TopBar(Left = {
                    Box(
                        Modifier
                            .size(50.dp)
                            .clickable { }, contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(id = R.string.menu), fontFamily = aliFontFamily)
                    }
                }, Right = {
                    Box(
                        Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable {
                                vm.isChooseDate = !vm.isChooseDate
                                if (!vm.isChooseDate) vm.indexDate = -1
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("时间")
                    }
                })
                val scope = rememberCoroutineScope()
                LazyRow(
                    Modifier
                        .pointerInput(Unit){
                            awaitPointerEventScope {
                                val event = awaitPointerEvent()
                            }
                        }
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    items(vm.tags.size) {
                        Box(
                            Modifier
                                .width(70.dp)
                                .fillMaxHeight()
                                .clickable { scope.launch { vm.pagerState.animateScrollToPage(it) } },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                vm.tags[it], color = if (vm.pagerState.currentPage == it)
                                    Color.White else Color(1f, 1f, 1f, 0.6f)
                            )
                        }
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    HorizontalPager(count = vm.tags.size, state = vm.pagerState) { i ->
                        VideoImg(vm, vm.screenVideo(i, vm.myDate))
                    }
                    ChooseDate(vm = vm, modifier = Modifier.align(Alignment.CenterEnd))
                }
            }
        }
        
    })
    
}

//时间筛选
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChooseDate(vm: Vm, modifier: Modifier) {
    val list = produceDates()
    AnimatedVisibility(
        modifier = modifier,
        visible = vm.isChooseDate,
        enter = scaleIn(),
        exit = scaleOut(),
    ) {
        Box(
            modifier
                .padding(10.dp)
                .width(50.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
        
        ) {
            Bg()
            LazyColumn(
                Modifier
                    .width(50.dp)
                    .padding(vertical = 10.dp)
            ) {
                items(list.size) {
                    val animate = remember { androidx.compose.animation.core.Animatable(0f) }
                    LaunchedEffect(key1 = Unit, block = {animate.animateTo(1f,animationSpec = tween(500))})
                    val modifier = Modifier.graphicsLayer(
                        scaleY = animate.value,
                        scaleX = animate.value,
                        alpha = animate.value
                    )
                    Box(
                        modifier
                            .fillMaxSize()
                            .height(30.dp), contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier
                                .width(40.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .clickable { vm.indexDate = it;vm.myDate = list[it] }
                                .background(
                                    if (it == vm.indexDate) Color(
                                        0f,
                                        0f,
                                        0f,
                                        0.2f
                                    ) else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${list[it].show}", fontSize = 13.sp,
                                fontWeight = if (list[it].month == -1) FontWeight.W700 else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
    
}

//图片列表
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun VideoImg(vm: Vm, list: ArrayList<Video>) {
    var isFresh by remember { mutableStateOf(false)}
    SwipeRefresh(
        state = rememberSwipeRefreshState(isFresh),
        onRefresh = {
            GlobalScope.launch {
                isFresh = true
                vm.allVideos = http.videos()
                isFresh = false
            }
        },
    ){
        LazyColumn(Modifier.fillMaxSize()) {
            val w = (screen.w.p2d - 8) / 3
            items(list.size / 3) {
                val animate = remember { androidx.compose.animation.core.Animatable(0f) }
                LaunchedEffect(key1 = Unit, block = {animate.animateTo(1f,animationSpec = tween(500))})
                val modifier = Modifier.graphicsLayer(
                    scaleY = animate.value,
                    scaleX = animate.value,
                    alpha = animate.value
                )
                Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    for (i in it * 3..it * 3 + 2) {
                        Box(
                            Modifier
                                .width(w.dp)
                                .height((1.3 * w).dp)
                                .padding(top = 2.dp)
                                .clickable { vm.navController.navigate("play/$i");vm.playVideos = list }
                        ) {
                            Image(
                                painter = rememberImagePainter(data = list[i].img),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                "${list.size - i}", modifier = Modifier
                                    .padding(5.dp)
                                    .align(Alignment.TopStart),
                                fontSize = 11.sp
                            )
                            Text(
                                list[i].myDescription, modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(all = 5.dp),
                                textAlign = TextAlign.Start,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
    
}