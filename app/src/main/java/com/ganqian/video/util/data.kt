package com.ganqian.video.util

class Storage {
    var indexTag = 0
    var indexVideo = 0
}

class MyDate(var show: Int = 0, var year: Int = 0, var month: Int = 0)

class Video {
    var description = ""
    var id = ""
    var url = ""
    var uri = ""
    var img = ""
    var imgUri = ""
    var createTime = 0L //时间戳 s
    var duration = 0 //ms
    
    var myDescription = ""
    var tag = ""
}