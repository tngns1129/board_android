package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import kotlin.random.Random

class Signup : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivitySignupBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    var signup:SignupData? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignupBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var signupService: SignupService = retrofit.create(SignupService::class.java)

        var uid : String
        var id : String? = null
        var pw: String?

        binding.cancel.setOnClickListener(){
            finish()
        }

        binding.signup.setOnClickListener(){
            id = binding.userid.text.toString()
            pw = binding.userpassward.text.toString()
            Log.d("SIGNUP","id : "+id)
            Log.d("SIGNUP","pw : "+pw)
            signupService.requestSignup(id!!, pw!!).enqueue(object: Callback<SignupData> {
                override fun onFailure(call: Call<SignupData>, t: Throwable) {
                    t.message?.let { it1 -> Log.e("SIGNUP", it1) }
                    signup?.let { it1 -> toast(it1.msg) }
                }
                override fun onResponse(call: Call<SignupData>, response: Response<SignupData>) {
                    signup = response.body()
                    Log.d("SIGNUP","msg : "+signup?.msg)
                    Log.d("SIGNUP","code : "+signup?.code)
                    signup?.let { it1 -> toast(it1.msg) }
                    if(signup?.code.equals("000")){
                        finish()
                    }
                }
            })

        }

    }
    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
}