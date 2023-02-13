package com.semo.myapplication

data class TitleViewData(
    var title: String?,
    var brief_description: String?,
    var updated_date: String?,
    var user:UserData,
    var id:Int?,
){
}