package com.semo.myapplication

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SignService {
    @GET("/sign")   //signin
    fun requestSignin(
        @Query("username") userid:String,
        @Query("password") userpw:String,
        @Query("token") token:String,
    ) : Call<SigninData>

    @FormUrlEncoded
    @POST("/sign")   //signup
    fun requestSignup(
        @Field("username") userid:String,
        @Field("password") userpw:String
    ) : Call<SignupData>

}