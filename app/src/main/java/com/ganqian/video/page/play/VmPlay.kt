package com.ganqian.video.page.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ganqian.aaa.mlog
import com.ganqian.video.Vm
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ExoPlayer

@OptIn(ExperimentalPagerApi::class)
class VmPlay(val vm:Vm):ViewModel(){
    val players = arrayOfNulls<ExoPlayer?>(vm.playVideos.size)
    var isPlay by mutableStateOf(true)
    var isShowState by mutableStateOf(false)
    var isPopBottom = mutableStateOf(false)
    lateinit var pagerState:PagerState
}

class VmPlayFactory(private val vm:Vm) :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VmPlay(vm) as T
    }
}