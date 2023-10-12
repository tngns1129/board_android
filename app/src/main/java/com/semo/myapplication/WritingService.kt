package com.semo.myapplication

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WritingService {
    @FormUrlEncoded
    @POST("/post/detail")
    fun requestPost(
        @Field("title") title: String?,
        @Field("content") contents: String?,
        @Field("user_id") author: String?,
    ) : Call<WritingData>
}