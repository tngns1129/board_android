package com.semo.myapplication

import android.os.Build
import android.os.Bundle
import android.os.PatternMatcher
import android.text.Spannable
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.semo.myapplication.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Matcher
import java.util.regex.Pattern

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

        val usePattern = Pattern.compile(resources.getString(R.string.terms_condition))
        val processPattern = Pattern.compile(resources.getString(R.string.processing_policy))

        val mTransform = Linkify.TransformFilter { match, url -> "" }
        Linkify.addLinks(binding.rules,usePattern,"https://sites.google.com/view/boardrodlswjdqh/%ED%99%88",null,mTransform)
        Linkify.addLinks(binding.rules,processPattern,"https://sites.google.com/view/boardrodlswjdqh/%ED%99%88",null,mTransform)

        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var signupService: SignupService = retrofit.create(SignupService::class.java)

        var uid : String
        var id : String? = null
        var pw: String?

        binding.cancel.setOnClickListener(){

            id = binding.userid.text.toString()
            pw = binding.userpassward.text.toString()


            finish()
        }

        binding.signup.setOnClickListener(){
            if(binding.check.isChecked) {
                binding.idRule.visibility = View.GONE
                binding.pwRule.visibility = View.GONE
                id = binding.userid.text.toString()
                pw = binding.userpassward.text.toString()
                Log.d("SIGNUP", "id : " + id)
                Log.d("SIGNUP", "pw : " + pw)

                val idPattern = "^[A-Za-z0-9]{1,15}+$"
                val pwPattern = "^[A-Za-z0-9]{1,15}+$"

                var pattern = Pattern.compile(idPattern)
                val idMatcher = pattern.matcher(id)
                pattern = Pattern.compile(pwPattern)
                val pwMatcher = pattern.matcher(pw)

                if (idMatcher.matches() && pwMatcher.matches()) {
                    signupService.requestSignup(id!!, pw!!).enqueue(object : Callback<SignupData> {
                        override fun onFailure(call: Call<SignupData>, t: Throwable) {
                            t.message?.let { it1 -> Log.e("SIGNUP", it1) }
                        }

                        override fun onResponse(
                            call: Call<SignupData>,
                            response: Response<SignupData>
                        ) {
                            signup = response.body()
                            Log.d("SIGNUP", "code : " + signup?.code)
                            if (signup?.code.equals("000")) {
                                finish()
                            } else if(signup?.code.equals("001")){
                                toast(resources.getString(R.string.exist_id))
                            }
                        }
                    })
                } else if (!idMatcher.matches() && pwMatcher.matches()) {
                    binding.idRule.visibility = View.VISIBLE
                } else if (idMatcher.matches() && !pwMatcher.matches()) {
                    binding.pwRule.visibility = View.VISIBLE
                } else if (!idMatcher.matches() && !pwMatcher.matches()) {
                    binding.idRule.visibility = View.VISIBLE
                    binding.pwRule.visibility = View.VISIBLE
                }
            } else{
                toast(resources.getString(R.string.signup_check_rule))
            }
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