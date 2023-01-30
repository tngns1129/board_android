package com.example.myapplication

data class BoardData(
    var id: Int,
    var title: String?,
    var brief_description: String?,
    var created_date: String?,
    var updated_date: String?,
    var user_id:String?,
    var post_id: String?,
    var content: String?,
    var large_content:String?,
    ){
}
data class CheckAuthorData(
    var code: String?,
    var msg: String?,
){
}

data class ContentViewData(
    var content: String?,
){
}