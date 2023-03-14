package com.semo.myapplication

import java.util.*

data class CommentData(
    var id:Int?,
    var content: String?,
    var updated_date: String?,
    var user:UserData?,
)
