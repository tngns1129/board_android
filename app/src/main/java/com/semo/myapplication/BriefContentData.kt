package com.semo.myapplication

import java.util.Date

data class BriefContentData(
    var title: String?,
    var brief_description: String?,
    var updated_date: String?,
    var user:UserData,
    var id:Int?,
    var comment_count:Int,
){
}