package com.ganqian.music.page.geDanSquare

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.ganqian.aaa.StatusBar
import com.ganqian.aaa.p2d
import com.ganqian.aaa.screen
import com.ganqian.compose.R
import com.ganqian.music.api.http
import com.ganqian.music.component.Control
import com.ganqian.music.data.Gedan
import com.ganqian.music.ui.aliFontFamily
import com.ganqian.music.ui.iconStyle
import com.ganqian.music.util.sp
import com.ganqian.music.viewModel.VmPlayer
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PageGeDanSquare(vm: VmPlayer) {
    val vmGeDan: VmGeDan = viewModel()
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit, block = {
        vmGeDan.highqualityTags()
        snapshotFlow { pagerState.currentPage }.collect { page ->
            vmGeDan.currentPage = page
        }
    })
    
    Column(Modifier.fillMaxSize()) {
        StatusBar()
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(id = R.string.back),
                modifier = Modifier.width(50.dp),
                style = iconStyle
            )
            Text(
                "歌单广场",
                fontWeight = FontWeight.W900
            )
        }
        LazyRow(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            items(vmGeDan.listTag.size) {
                Box(
                    Modifier
                        .width(70.dp)
                        .fillMaxHeight()
                        .clickable {
                            vmGeDan.currentPage = it
                            scope.launch {
                                withContext(Dispatchers.Main) {
                                    pagerState.animateScrollToPage(it)
                                }
                            }
            
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        vmGeDan.listTag[it].name,
                        fontWeight = FontWeight.W700,
                        color = if (vmGeDan.currentPage == it) Color.White
                        else Color(
                            1f, 1f, 1f, 0.6f
                        )
                    )
                }
            }
        }
        HorizontalPager(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            count = vmGeDan.listTag.size,
            state = pagerState
        ) {
            for ((i, v) in vmGeDan.listTag.withIndex()) {
                if (it == i) {
                    FlowRow(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                        crossAxisSpacing = 10.dp,
                    ) {
                        val list = remember { mutableStateOf(ArrayList<Gedan>()) }
                        LaunchedEffect(key1 = Unit, block = {
                            if (i == 0) {
                                list.value = http.personalized()
                            } else {
                                list.value = http.topPlaylistHighquality(v.name)
                            }
                            
                        })
                        for ((i, v) in list.value.withIndex()) {
                            Item(vmGeDan, vm, v)
                        }
                    }
                }
            }
        }
        Control(vm = vm)
    }
}

@Composable
fun Item(vmGeDan: VmGeDan, vm: VmPlayer, v: Gedan) {
    val w = screen.w.p2d - 20
    Box(
        Modifier
            .width(((w - 15) / 3).dp)
            .height((((w - 15) / 3) + 40).dp)
            .clickable {
                sp.edit().putString("geDanP", Gson().toJson(v)).apply()
                vm.navController.navigate("geDanDetail/geDan")
            }
    ) {
        Image(
            painter = rememberImagePainter(
                data = v.img
            ),
            modifier = Modifier
                .size(((w - 15) / 3).dp)
                .clip(RoundedCornerShape(10.dp))
                .align(Alignment.TopCenter),
            contentDescription = null
        )
        Row(
            Modifier
                .height(20.dp)
                .offset(x = -7.dp, y = 7.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0f, 0f, 0f, 0.2f))
                .padding(horizontal = 10.dp)
                .align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(id = R.string.play1),
                style = iconStyle,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(
                v.playCount,
                fontSize = 12.sp,
            )
        }
        Text(
            v.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.BottomCenter),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
}