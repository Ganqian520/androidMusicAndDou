package com.ganqian.music.page.geDanSquare

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ganqian.music.api.http
import com.ganqian.music.data.Gedan
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("MutableCollectionMutableState")

class VmGeDan :ViewModel(){
//    var pagerState = PagerState(initialPage = 0)
    var listTag by mutableStateOf(ArrayList<Gedan>())
   var currentPage by mutableStateOf(0)
    
    fun highqualityTags(){
        GlobalScope.launch {
            listTag = http.highqualityTags()
            val gedan = Gedan()
            gedan.name = "推荐"
            listTag.add(0,gedan)
        }
    }
    
}