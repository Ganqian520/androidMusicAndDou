package com.ganqian.music.page.search

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ganqian.music.api.http
import com.ganqian.music.data.Song
import com.ganqian.music.util.sp
import com.ganqian.music.viewModel.VmPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("MutableCollectionMutableState")
class VmSearch(val vm: VmPlayer) : ViewModel() {
    var platform by mutableStateOf("net")
    var keywords by mutableStateOf("")
    var state by mutableStateOf(1) //热搜 预搜 结果
    var hots by mutableStateOf(ArrayList<String>()) //热搜词
    var listNet by mutableStateOf(ArrayList<Song>())
    var prepares by mutableStateOf(ArrayList<String>())  //建议词
    var history by mutableStateOf(ArrayList<String>())
    
    fun getPrepareKeywords() {
        GlobalScope.launch {
            prepares = http.getPrepareKeywords(keywords)
        }
    }
    
    fun getHotKeywords() {
        GlobalScope.launch {
            hots = http.getHotKeywords()
        }
    }
    
    fun searchNet() {
        if (keywords == "") return
        listNet.clear()
        state = 3
        history.add(0, keywords)
        val last = history.lastIndexOf(keywords)
        if (last != 0) history.removeAt(last)
        GlobalScope.launch {
            if (platform == "net") {
                vm.listOther = http.searchNet(keywords)
                listNet = vm.listOther
            }
        }
    }
    
    fun lanuch() {
        getHotKeywords()
        val historyStr = sp.getString("historySearch", "")
        if (historyStr != "") {
            history =
                Gson().fromJson(historyStr, object : TypeToken<ArrayList<String>>() {}.type)
        }
    }
    
    fun dispose() {
        sp.edit().putString("historySearch", Gson().toJson(history)).apply()
    }
}

class VmSearchFactory(private val vm: VmPlayer) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VmSearch(vm) as T
    }
}
