package com.ganqian.music.page.bang

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.ganqian.aaa.StatusBar
import com.ganqian.aaa.p2d
import com.ganqian.aaa.screen
import com.ganqian.compose.R
import com.ganqian.music.api.http
import com.ganqian.music.component.Control
import com.ganqian.music.data.Gedan
import com.ganqian.music.ui.iconStyle
import com.ganqian.music.util.sp
import com.ganqian.music.viewModel.VmPlayer
import com.google.accompanist.flowlayout.FlowRow
import com.google.gson.Gson

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PageTopList(vmPlayer: VmPlayer) {
    var list by remember { mutableStateOf(ArrayList<Gedan>()) }
    LaunchedEffect(key1 = Unit, block = {
        list = http.toplistDetail()
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
                "排行榜",
                fontWeight = FontWeight.W900
            )
        }
        LazyColumn(Modifier.weight(1f)) {
            if (list.size == 0) return@LazyColumn
            items(4) {
                val v = list[it]
                Row(
                    Modifier
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0f, 0f, 0f, 0.1f))
                        .clickable {
                            sp.edit().putString("geDanP",Gson().toJson(v)).apply()
                            vmPlayer.navController.navigate("geDanDetail/geDan")
                        },
                ) {
                    Column(Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
                        Text(
                            v.name,
                            fontWeight = FontWeight.W900,
                            fontSize = 18.sp,
                        )
                        Image(
                            painter = rememberImagePainter(data = v.img),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .size(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                    Column(
                        Modifier.fillMaxSize()
                    ) {
                        Text(
                            v.updateFrequency,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, end = 10.dp),
                            textAlign = TextAlign.End,
                            fontSize = 11.sp
                        )
                        val arr = v.abstract.split(",")
                        for (i in arr.indices) {
                            Row(Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
                                val arr1 = arr[i].split("\n")
                                Text(
                                    "${i + 1}. ${arr1[0]}",
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Start,
                                    maxLines = 1
                                )
                                Text(
                                    " - ${arr1[1]}",
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color(1f, 1f, 1f, 0.5f),
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
            val w = (screen.w.p2d - 10) / 3 - 10
            item {
                FlowRow(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 5.dp),
                ) {
                    for (i in 4..list.size - 1) {
                        Column() {
                            Box(
                                Modifier
                                    .padding(5.dp)
                                    .width(w.dp)
                                    .height((50 + w).dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0f, 0f, 0f, 0.1f))
                                    .clickable {
                                        sp.edit().putString("geDanP",Gson().toJson(list[i])).apply()
                                        vmPlayer.navController.navigate("geDanDetail/geDan")
                                    }
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = list[i].img),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size((w - 20).dp)
                                        .clip(
                                            RoundedCornerShape(10.dp)
                                        )
                                )
                                Text(
                                    list[i].updateFrequency,
                                    modifier = Modifier
                                        .padding(top = 15.dp, end = 15.dp)
                                        .align(
                                            Alignment.TopEnd
                                        ),
                                    fontSize = 11.sp
                                )
                                Box(
                                    Modifier
                                        .height(50.dp)
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                                        .align(Alignment.BottomCenter),
                                    contentAlignment = Alignment.TopStart
                                ){
                                    Text(
                                        list[i].name,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Control(vm = vmPlayer)
    }
}
