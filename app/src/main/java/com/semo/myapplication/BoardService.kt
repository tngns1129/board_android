package com.semo.myapplication

import retrofit2.Call
import retrofit2.http.*

interface BoardService {

    @GET("/titleview/")
    fun titleview(
    ) : Call<List<TitleViewData>>

    @FormUrlEncoded
    @POST("/contentview/")
    fun contentview(
        @Field("post_id") postid:Int?,
    ) : Call<ContentViewData>

    @FormUrlEncoded
    @POST("/delete/")
    fun delete(
        @Field("post_id") postid:Int,
        @Field("user_id") userid:String?,
    ) : Call<DeleteData>

    @GET("/posts/")
    fun getlist(
    ) : Call<List<BoardData>>

    @FormUrlEncoded
    @POST("/modify/")
    fun modify(
        @Field("post_id") postid:Int,
        @Field("user_id") userid: String?,
        @Field("title") title: String?,
        @Field("content") contents: String?,
    ) : Call<ModyfiyData>

    @FormUrlEncoded
    @POST("/checkauthorview/")
    fun checkauthor(
        @Field("post_id") postid:Int,
        @Field("user_id") userid: String?,
    ) : Call<CheckAuthorData>

}