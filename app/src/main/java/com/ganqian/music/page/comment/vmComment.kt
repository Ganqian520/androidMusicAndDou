package com.ganqian.music.page.comment

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ganqian.music.api.api
import com.ganqian.music.api.http
import com.ganqian.music.data.Comment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("MutableCollectionMutableState")
class VmComment(val id:String):ViewModel(){
    var total by mutableStateOf(0)
    var listL = ArrayList<Comment>()
    var listR =ArrayList<Comment>()
    
    suspend fun getComment(){
        val list = http.getComment(id)
        list.forEachIndexed{i,v->
            if(i%2 ==0) listL.add(v) else listR.add(v)
        }
        total = 100
    }
    
    fun likeComment(cid:String){
        GlobalScope.launch {
            api.instance.likeComment(id,cid)
        }
    }
    
}

class VmCommentFactory(private val id:String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VmComment(id) as T
     }
}

