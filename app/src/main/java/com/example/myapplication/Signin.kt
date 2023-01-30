package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Signin : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityLoginBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    var signin:SigninData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var signinService: SigninService = retrofit.create(SigninService::class.java)

        binding.signup.setOnClickListener(){
            val signupIntent = Intent(this, Signup::class.java)
            startActivity(signupIntent)
        }
        binding.login.setOnClickListener(){
            val mainIntent = Intent(this, BoardList::class.java)
            //startActivity(mainIntent)
            //finish()

            var id = mBinding!!.userid.text.toString()
            var pw = mBinding!!.userpassward.text.toString()
            signinService.requestSignin(id,pw).enqueue(object: Callback<SigninData>{
                override fun onFailure(call: Call<SigninData>, t: Throwable) {
                    t.message?.let { it1 -> Log.e("LOGIN", it1) }
                    toast("서버 연결 실패")
                }
                override fun onResponse(call: Call<SigninData>, response: Response<SigninData>) {
                    signin = response.body()
                    Log.d("SIGNIN","msg : "+signin?.msg)
                    Log.d("SIGNIN","code : "+signin?.code)
                    Log.d("SIGNIN", "uid : "+ signin?.uid)
                    signin?.let { it1 -> toast(it1.msg) }
                    when(signin?.code){
                        "000" -> { //성공
                            val user = User(signin!!.uid, id)
                            mainIntent.putExtra("user", user)
                            startActivity(mainIntent)
                            finish()
                        }
                        "001" -> { //id 불일치
                            mBinding!!.userid.setText("")
                            mBinding!!.userpassward.setText("")
                        }
                        "002" -> { //pw 불일치
                            mBinding!!.userpassward.setText("")
                        }
                    }
                }
            })
        }

    }
    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
