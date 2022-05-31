package com.ganqian.music.page.geDanDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ganqian.aaa.StatusBar
import com.ganqian.compose.R
import com.ganqian.music.api.http
import com.ganqian.music.component.Control
import com.ganqian.music.data.Gedan
import com.ganqian.music.data.Song
import com.ganqian.music.ui.aliFontFamily
import com.ganqian.music.util.sp
import com.ganqian.music.view.SongList
import com.ganqian.music.viewModel.VmPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun PageGeDanDetail(from:String,vm:VmPlayer){
    var title by remember{ mutableStateOf("") }
    LaunchedEffect(key1 = Unit, block = {
        when(from){
            "dayAdvice" -> {
                vm.listOther = http.getDayAdvice()
            }
            "history" -> {
                val list_ =sp.getString("history","")
                if(list_!="") vm.listOther = Gson().fromJson(list_,object : TypeToken<ArrayList<Song>>() {}.type)
            }
            "geDan" ->{
                val geDan_ = sp.getString("geDanP","")
                val geDan = Gson().fromJson(geDan_,Gedan::class.java)
                title = geDan.name
                vm.listOther = http.getSongs(geDan.id)
            }
        }
    })
    Column(Modifier.fillMaxSize()) {
        StatusBar()
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                stringResource(id = R.string.back),
                fontFamily = aliFontFamily,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = when(from){
                    "dayAdvice" -> "每日推荐"
                    "history" -> "最近播放"
                    else -> title
                }
            )
        }
        SongList(vm = vm, list =vm.listOther , mode = "other",Modifier.weight(1f))
        Control(vm = vm)
    }
}