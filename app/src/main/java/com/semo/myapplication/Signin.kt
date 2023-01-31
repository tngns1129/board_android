package com.semo.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.semo.myapplication.databinding.ActivitySigninBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class Signin : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivitySigninBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    var signin:SigninData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySigninBinding.inflate(layoutInflater)
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
            binding.idRule.visibility = View.GONE
            binding.pwRule.visibility = View.GONE

            var id = mBinding!!.userid.text.toString()
            var pw = mBinding!!.userpassward.text.toString()

            val idPattern = "^[A-Za-z0-9]{1,15}+$"
            val pwPattern = "^[A-Za-z0-9]{1,15}+$"

            var pattern = Pattern.compile(idPattern)
            val idMatcher = pattern.matcher(id)
            pattern = Pattern.compile(pwPattern)
            val pwMatcher = pattern.matcher(pw)

            if(idMatcher.matches() && pwMatcher.matches()) {
                signinService.requestSignin(id, pw).enqueue(object : Callback<SigninData> {
                    override fun onFailure(call: Call<SigninData>, t: Throwable) {
                        t.message?.let { it1 -> Log.e("LOGIN", it1) }
                        toast("서버 연결 실패")
                    }
                    override fun onResponse(
                        call: Call<SigninData>,
                        response: Response<SigninData>
                    ) {
                        signin = response.body()
                        Log.d("SIGNIN", "msg : " + signin?.msg)
                        Log.d("SIGNIN", "code : " + signin?.code)
                        Log.d("SIGNIN", "uid : " + signin?.uid)
                        signin?.let { it1 -> toast(it1.msg) }
                        when (signin?.code) {
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
            }else if(!idMatcher.matches() && pwMatcher.matches()){
                binding.idRule.visibility = View.VISIBLE
            } else if(idMatcher.matches() && !pwMatcher.matches()){
                binding.pwRule.visibility = View.VISIBLE
            } else if(!idMatcher.matches() && !pwMatcher.matches()){
                binding.idRule.visibility = View.VISIBLE
                binding.pwRule.visibility = View.VISIBLE
            }
        }

    }
    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
