package com.ganqian.video.util

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.ganqian.aaa.*
import com.ganqian.compose.R
import com.ganqian.video.VideoActivity
import com.ganqian.video.Vm
import com.ganqian.video.aliFontFamily
import com.ganqian.video.iconStyle
import kotlinx.coroutines.launch

//底部弹窗
@Composable
fun PopBottom(){

}

//抽屉
@Composable
fun VideoDrawer(vm: Vm) {
    Column(Modifier.fillMaxSize()) {
        StatusBar()
        Box(
            Modifier.height(150.dp),
            contentAlignment = Alignment.Center
        ) { Activities(activityStr = "video") }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("监听粘贴事件")
            Switch(checked = vm.isFloatService, onCheckedChange = {vm.handleService()})
        }
        Row(Modifier.clickable {
            vm.pushVideos()
        }.fillMaxWidth().height(50.dp).padding(horizontal = 15.dp),verticalAlignment = Alignment.CenterVertically){
            Text(when(vm.statePush){
                0 -> "推送视频到云端"
                1 -> "推送中..."
                else -> "失败请重试"
            })
        }
        Row(Modifier.clickable {
            vm.navController.navigate("editTag")
        }.fillMaxWidth().height(50.dp).padding(horizontal = 15.dp),verticalAlignment = Alignment.CenterVertically){
            Text("编辑标签")
        }
    }
}

//底部控件 打开播放页 记录上次播放
@Composable
fun Control() {

}

//返回
@Composable
fun Back(vm: Vm) {
    Box(
        Modifier
            .size(50.dp)
            .clickable { vm.navController.popBackStack() }, contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(id = R.string.back),
            style = iconStyle
        )
    }
}

//顶部栏
@Composable
fun TopBar(
    Left: @Composable () -> Unit = {},
    Middle: @Composable () -> Unit = {},
    Right: @Composable () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Left()
        Middle()
        Right()
    }
}
