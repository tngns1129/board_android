package com.semo.myapplication

import retrofit2.Call
import retrofit2.http.*

interface BoardService {

    @GET("/post/")
    fun titleview(
        @Query("page") page: Int,
    ) : Call<List<BriefContentViewData>>

    @GET("/post/detail/{post_id}")
    fun contentview(
        @Path("post_id") postid:Int?,
    ) : Call<ContentViewData>

    @DELETE("/post/detail/{post_id}")
    fun delete(
        @Path("post_id") postid:Int,
        @Query("user_id") userid:String?,
    ) : Call<DeleteData>

    @PATCH("/post/detail/{post_id}")
    fun modify(
        @Path("post_id") postid:Int,
        @Query("user_id") userid: String?,
        @Query("title") title: String?,
        @Query("content") contents: String?,
    ) : Call<ModifyData>

    @FormUrlEncoded
    @POST("/post/checkauthor")
    fun checkauthor(
        @Field("post_id") postid: Int,
        @Field("user_id") userid: String?,
    ) : Call<CheckAuthorData>

    @GET("/post/comments")
    fun commentview(
        @Query("post_id") postid: Int,
    ) : Call<CommentViewData>

    @FormUrlEncoded
    @POST("/post/comments")
    fun writecommentview(
        @Field("post_id") postid: Int,
        @Field("user_id") userid: String?,
        @Field("content") title: String?,
    ) : Call<CommentWriteData>

    @DELETE("/post/comments")
    fun deletecommentview(
        @Query("id") id:Int?,
        @Query("user_id") userid: String?,
    ) : Call<DeleteData>
}