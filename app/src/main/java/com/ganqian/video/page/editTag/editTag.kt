package com.ganqian.video.page.editTag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ganqian.aaa.Drawer
import com.ganqian.aaa.StatusBar
import com.ganqian.aaa.mlog
import com.ganqian.video.Vm
import com.ganqian.video.util.Back
import com.ganqian.video.util.TopBar

@Composable
fun ScreenEditTag(vm: Vm) {
    
    Column(Modifier.fillMaxSize()) {
        StatusBar()
        TopBar(Left = { Back(vm) },
            Middle = { Text("编辑标签") },
            Right = {
                Box(
                    Modifier
                        .size(50.dp)
                        .clickable {
            
                        }, contentAlignment = Alignment.Center
                ) {
                    Text("确定")
                }
            })
        Column(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit){
                    detectDragGestures(
                        onDrag = { a,b->
                            mlog("父")}
                    )
                }
                .verticalScroll(rememberScrollState())) {
            Text(
                "头部", modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            LazyColumn(Modifier.height(500.dp), content = {
                items(10) {
                    Text(
                        "$it", modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            })
            LazyColumn(Modifier.height(500.dp), content = {
                items(10) {
                    Text(
                        "$it", modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            })
        }
    }
    
}