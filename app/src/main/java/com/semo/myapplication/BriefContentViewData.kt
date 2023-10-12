package com.semo.myapplication

data class BriefContentViewData(
    var id:Int,
    var title:String,
    var brief_description:String,
    var created_date:String,
    var updated_date:String,
    var comment_count:Int,
    var delete:String,
    var user:UserData,
)
