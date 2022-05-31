package com.ganqian.video.page.floatingWindow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ganqian.aaa.*
import com.ganqian.music.ui.Theme_
import com.ganqian.video.Vm
import com.ganqian.video.api.http
import com.ganqian.video.util.randomBush
import com.ganqian.video.util.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun FloatingWindow(control: ServiceFloatVideo.Control) {
    val w = screen.w.p2d - 30
    var index by remember { mutableStateOf(0) }
    var description by remember { mutableStateOf("") }
    var isOk by remember { mutableStateOf(false) }
    var isWait by remember { mutableStateOf(false) }
    val tagsStorage = sp.getString("tags", "")
    val share = sp.getString("share", "")!!
    var list = ArrayList<String>()
    if (tagsStorage != "") {
        list = Gson().fromJson(tagsStorage, object : TypeToken<ArrayList<String>>() {}.type)
    }
    list.add("待定")
    var brush by remember { mutableStateOf(randomBush())}
    LaunchedEffect(key1 = Unit, block = {
        index = sp.getInt("floatingIndex", 0)
        brush = randomBush()
    })
    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            sp.edit().putInt("floatingIndex", index).apply()
        }
    })
    Theme_ {
        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))) {
            Bg()
            Column(
                Modifier.fillMaxSize()
            ) {
                FlowRow(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .fillMaxWidth(),
                    mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
                ) {
                    for ((i, v) in list.withIndex()) {
                        Box(
                            Modifier
                                .width((w / 5).dp)
                                .height(50.dp)
                                .clickable {
                                    index = i
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(v, fontWeight = if (i == index) FontWeight.W900 else FontWeight.Normal)
                        }
                    }
                }
                TextField(
                    value = description, onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 15.dp),
                    placeholder = {Text("输入描述语：")},
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                    ),
                )
                Row(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clickable { control.closeWindow() }, contentAlignment = Alignment.Center
                    ) {
                        Text("关闭")
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clickable {
                                if (share != "" && !isWait) {
                                    isWait = true
                                    GlobalScope.launch {
                                        isOk = http.handleShare(share, list[index], description)
                                        isWait = false
                                        if (isOk) control.closeWindow()
                                    }
                                }
                            }, contentAlignment = Alignment.Center
                    ) {
                        Text(if (!isWait) "确定" else "解析中...")
                    }
                }
            }
        }
    }
    
}