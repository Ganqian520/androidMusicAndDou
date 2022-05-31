package com.ganqian.music.data

//缓存
class Cache {
    var indexNet = -1   //歌曲序号
    var indexDou = -1
    var indexSongList = 0 //列表序号
    var douTag = 0
    var order = 0 //播放顺序
    var userNet = UserNet("","","") //网易用户
    var song = Song() //上次播放歌曲
}

//可以当作歌单标签 歌单详情 用户 榜单
class Gedan {
    var name = ""
    var img = ""
    var playCount = ""
    var id = ""
    var description = ""
    var updateFrequency = ""
    var abstract = "" //榜单摘要 “name - author,name - author,name - author,”
}

//评论
class Comment {
    var img = ""
    var name = ""
    var time = ""
    var likes = 0
    var liked = false
    var id = ""
    var content = ""
}
//歌词
class Lyric {
    var format = "00:00"
    var millisecond = 0
    var content = ""
}
//歌曲
class Song{
    var name: String = ""
    var author: String = ""
    var id: String = ""
    var duration = 0    //秒数
    var img: String = ""
    var url: String = ""
    var tag: Int = 1 //抖音独有
    var platform: String ="" //net dou
}
//用户
class UserNet(
    var nickName:String="",
    var id:String="",
    var avatarUrl:String="",
    var phone: String ="",
    var password:String=""
    )
//歌单
data class SongList(
    var id: String, //抖音对应的就是tag
    var name:String,
    var img:String,
)

