package com.ganqian.music.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganqian.compose.R
import com.ganqian.music.api.http
import com.ganqian.music.data.Song
import com.ganqian.music.ui.aliFontFamily
import com.ganqian.music.util.util
import com.ganqian.music.viewModel.VmPlayer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//带刷新
@OptIn(ExperimentalComposeUiApi::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
@Composable
fun SongListFresh(vm: VmPlayer, list: List<Song>, mode: String,fresh: suspend () -> Unit = {}) {
    var isFresh by remember { mutableStateOf(false) }
    val index = when(mode){
        "net" -> vm.indexNet
        "dou" -> vm.indexDou
        else -> vm.indexOther
    }
    SwipeRefresh(
        state = rememberSwipeRefreshState(isFresh),
        onRefresh = {
            GlobalScope.launch {
                isFresh = true
                fresh()
                isFresh = false
            }
        },
    ) {
        LazyColumn {
            items(list.size) {
                Row(
                    modifier = Modifier
                        .clickable {
                            vm.isShowPlay = true
                            vm.mode = mode
                            vm.indexFromList = it
                            vm.start(list[it])
                        }
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${list.size - it}",
                        modifier = Modifier.width(50.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = if(it==index) FontWeight.W800 else FontWeight.Normal
                    )
                    Column(
                        modifier = Modifier.weight(1F),
                    ) {
                        Text(
                            text = list[it].name,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = list[it].author,
                            color = Color(1f, 1f, 1f, 0.5f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = util.transTime(list[it].duration),
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                    Text(
                        text = stringResource(id = R.string.add),
                        fontFamily = aliFontFamily,
                        modifier = Modifier
                            .width(30.dp)
                            .clickable {
                                vm.listWant.add(list[it])
                            }
                            .motionEventSpy {
                                if (it.action == 1) {
                                
                                }
                            },
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(id = R.string.more),
                        fontFamily = aliFontFamily,
                        modifier = Modifier.width(40.dp).clickable { vm.handleItemMore(mode,list[it]) },
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
//不带刷新
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SongList(vm: VmPlayer, list: List<Song>, mode: String,modifier: Modifier=Modifier) {
    val index = when(mode){
        "net" -> vm.indexNet
        "dou" -> vm.indexDou
        else -> vm.indexOther
    }
    LazyColumn(
        modifier.nestedScroll(
            object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    return super.onPostScroll(consumed, available, source)
                }
            },
        )
    ) {
        items(list.size) {
            Row(
                modifier = Modifier
                    .clickable {
                        vm.isShowPlay = true
                        vm.mode = mode
                        vm.indexFromList = it
                        vm.start(list[it])
                    }
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${list.size - it}",
                    modifier = Modifier.width(50.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = if(it==index) FontWeight.W800 else FontWeight.Normal
                )
                Column(
                    modifier = Modifier.weight(1F),
                ) {
                    Text(
                        text = list[it].name,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = list[it].author,
                        color = Color(1f, 1f, 1f, 0.5f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = util.transTime(list[it].duration),
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
                Text(
                    text = stringResource(id = R.string.add),
                    fontFamily = aliFontFamily,
                    modifier = Modifier
                        .width(30.dp)
                        .clickable {
                            vm.listWant.add(list[it])
                        }
                        .motionEventSpy {
                            if (it.action == 1) {
            
                            }
                        },
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(id = R.string.more),
                    fontFamily = aliFontFamily,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

