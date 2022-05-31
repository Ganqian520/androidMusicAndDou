package com.ganqian.music.page.floatingWindow


import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ganqian.aaa.Bg
import com.ganqian.aaa.p2d
import com.ganqian.aaa.screen
import com.ganqian.music.MusicActivity
import com.ganqian.music.api.httpDou
import com.ganqian.music.ui.Theme_
import com.ganqian.music.util.sp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ListenMusic(control: ServiceListenMusic.Control) {
    val share = sp.getString("share","")!!
    var description by remember { mutableStateOf("")}
    var isVoice by remember { mutableStateOf(false)} //是否分为人声
    var state by remember { mutableStateOf(0)} //0开始 1解析中 2解析失败
    Theme_ {
        Box(Modifier.clip(RoundedCornerShape(15.dp))) {
            Bg(MusicActivity.context as Activity)
            Column(
                Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
        
                TextField(
                    value = description, onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .height(50.dp),
                    placeholder = { Text(text = "输入歌名：") },
                    textStyle = TextStyle(textAlign = TextAlign.Start),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                    ),
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 15.dp),verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween){
                    Text("是否有人声")
                    Switch(checked = isVoice, onCheckedChange = {isVoice=it})
                }
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
                                if (share != "" && state != 1) {
                                    state = 1
                                    GlobalScope.launch {
                                        val isOk = httpDou.handleShareMusic(share, description, isVoice)
                                        if (isOk) {
                                            state = 0
                                            control.closeWindow()
                                        } else {
                                            state = 2
                                        }
                                    }
                                }
                            }, contentAlignment = Alignment.Center
                    ) {
                        Text(when(state){
                            0 -> "开始解析"
                            1 -> "解析中..."
                            else -> "失败请重试"
                        })
                    }
                }
            }
        }
    }
    
}