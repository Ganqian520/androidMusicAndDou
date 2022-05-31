package com.ganqian.music.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ganqian.aaa.Activities
import com.ganqian.music.viewModel.VmLogin
import com.ganqian.music.viewModel.VmPlayer
import com.ganqian.video.Vm
import com.ganqian.video.page.play.VmPlay

//通知栏
@Composable
fun NoticeBar() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Cyan)
    )
}

//抽屉
@Composable
fun MusicDrawer(vm:VmPlayer,vmLogin: VmLogin) {
    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier.height(150.dp),
            contentAlignment = Alignment.Center
        ) { Activities(activityStr = "music") }
        Row(Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .height(50.dp),verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
            Text("网易云音乐：${if(vmLogin.stateLoginNet==3) vmLogin.user.nickName else ""}" )
            Text(if(vmLogin.stateLoginNet==3) "退出" else "",modifier = Modifier.clickable { vmLogin.quit("net") })
        }
        Row(Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .height(50.dp),verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text("GQ账号：${if(vmLogin.stateLoginGQ==3) vmLogin.phoneGQ else ""}" )
            Text(if(vmLogin.stateLoginGQ==3) "退出" else "",modifier = Modifier.clickable { vmLogin.quit("dou") })
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("监听粘贴事件")
            Switch(checked = vm.isOpenListen, onCheckedChange = {vm.handleListen()})
        }
        Row(
            Modifier
                .clickable { vm.pushMusics() }
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .height(50.dp),verticalAlignment = Alignment.CenterVertically){
            Text(when(vm.statePushMusic){
                0 -> "推送抖音歌曲到云端"
                1 -> "推送中..."
                else -> "失败请重试"
            })
        }
    }
}