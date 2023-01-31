package com.semo.myapplication

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SigninService {
    @FormUrlEncoded
    @POST("/signin/")
    fun requestSignin(
        @Field("username") userid:String,
        @Field("password") userpw:String
    ) : Call<SigninData>
}