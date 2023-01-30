package com.example.myapplication

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface SignupService {
    @FormUrlEncoded
    @POST("/signup/")
    fun requestSignup(
        @Field("username") userid:String,
        @Field("password") userpw:String
    ) : Call<SignupData>
}