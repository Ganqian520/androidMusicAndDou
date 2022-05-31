package com.ganqian.music.page.comment

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.ganqian.aaa.StatusBar
import com.ganqian.aaa.aautil
import com.ganqian.compose.R
import com.ganqian.music.data.Comment
import com.ganqian.music.ui.aliFontFamily
import com.ganqian.music.ui.iconStyle

@Composable
fun PageComment(id:String) {
    val vm: VmComment = viewModel(factory = VmCommentFactory(id))
    LaunchedEffect(key1 = Unit, block = {
        vm.getComment()
    })
    Column {
        StatusBar()
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.back),
                style = iconStyle,
                modifier = Modifier.width(50.dp)
            )
            Text(
                text = "${vm.total}",
                fontWeight = FontWeight.W800
            )
        }
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for(it in vm.listL.indices){
                    Item(list = vm.listL, it = it,vm)
                }
            }
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for(it in vm.listR.indices){
                    Item(list = vm.listR, it = it,vm)
                }
            }
        }
    }
}

@Composable
fun Item(list:ArrayList<Comment>,it:Int,vm:VmComment){
    Column(
        Modifier
            .padding(horizontal = 5.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color(0f, 0f, 0f, 0.1f))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(
                    data = list[it].img,
                ),
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(50)),
                contentDescription = null,
            )
            Column {
                Text(
                    list[it].name,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 5.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W700
                )
                Text(
                    list[it].time,
                    color = Color(1f,1f,1f,0.4f),
                    fontSize = 12.sp
                )
            }
        }
        Text(
            list[it].content,
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 10.dp,end = 5.dp,top = 10.dp,bottom = 10.dp)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                "查看回复 >",
                fontSize = 12.sp
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.height(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "${list[it].likes}",
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .weight(3f),
                    fontSize = 12.sp
                )
                Text(
                    stringResource(id = R.string.zan),
                    fontFamily = aliFontFamily,
                    textAlign = TextAlign.End,
                    modifier = Modifier.clickable { vm.likeComment(list[it].id) },
                    fontSize = 15.sp
                )
            }
        }
    }
}