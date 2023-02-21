package com.semo.myapplication

import android.content.Intent
import android.content.SharedPreferences
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

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivitySigninBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    var signin:SigninData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE)
        editor = sharedPreferences.edit()

//        binding.userid.setText(sharedPreferences.getString("id",""))
//        binding.userpassward.setText(sharedPreferences.getString("pw",""))

        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var signinService: SigninService = retrofit.create(SigninService::class.java)
        val mainIntent = Intent(this, BoardList::class.java)

        binding.loginView.visibility = View.GONE
        binding.semo.visibility = View.VISIBLE
        binding.loading.visibility = View.VISIBLE

        var id = sharedPreferences.getString("id","")
        var pw = sharedPreferences.getString("pw","")

        if (id != null) {
            if (pw != null) {
                signinService.requestSignin(id, pw).enqueue(object : Callback<SigninData> {
                    override fun onFailure(call: Call<SigninData>, t: Throwable) {
                        t.message?.let { it1 -> Log.e("LOGIN", it1) }
                        toast(resources.getString(R.string.server_adress))
                        binding.loginView.visibility = View.VISIBLE
                        binding.semo.visibility = View.GONE
                        binding.loading.visibility = View.GONE
                    }

                    override fun onResponse(
                        call: Call<SigninData>,
                        response: Response<SigninData>
                    ) {
                        signin = response.body()
                        Log.d("SIGNINnn", "response : \n" + signin)
                        when (signin?.code) {
                            "000" -> { //성공
                                toast(resources.getString(R.string.autologin))
                                val user = UserData(signin!!.user.id, id)
                                mainIntent.putExtra("user", user)
                                startActivity(mainIntent)
                                finish()
                            }
                            "001" -> { //id 불일치
                                toast(resources.getString(R.string.autologinfail)+"\n"+resources.getString(R.string.checkid))
                                mBinding!!.userid.setText("")
                                mBinding!!.userpassward.setText("")
                                binding.loginView.visibility = View.VISIBLE
                                binding.semo.visibility = View.GONE
                                binding.loading.visibility = View.GONE
                            }
                            "002" -> { //pw 불일치
                                toast(resources.getString(R.string.autologinfail)+"\n"+resources.getString(R.string.checkpw))
                                mBinding!!.userpassward.setText("")
                                binding.loginView.visibility = View.VISIBLE
                                binding.semo.visibility = View.GONE
                                binding.loading.visibility = View.GONE
                            }
                            else -> {
                                binding.loginView.visibility = View.VISIBLE
                                binding.semo.visibility = View.GONE
                                binding.loading.visibility = View.GONE
                            }
                        }
                    }
                })
            }
        }

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
                        toast(resources.getString(R.string.serverfail))
                    }
                    override fun onResponse(
                        call: Call<SigninData>,
                        response: Response<SigninData>
                    ) {
                        signin = response.body()
                        Log.d("SIGNINnn", "response : \n" + signin)
                        when (signin?.code) {
                            "000" -> { //성공
                                toast(resources.getString(R.string.loginsuccess))
                                editor.putString("id", mBinding!!.userid.text.toString())
                                editor.putString("pw", mBinding!!.userpassward.text.toString())
                                editor.commit()
                                val user = UserData(signin!!.user.id, id)
                                mainIntent.putExtra("user", user)
                                startActivity(mainIntent)
                                finish()
                            }
                            "001" -> { //id 불일치
                                toast(resources.getString(R.string.checkid))
                                mBinding!!.userid.setText("")
                                mBinding!!.userpassward.setText("")
                            }
                            "002" -> { //pw 불일치
                                toast(resources.getString(R.string.checkpw))
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

    override fun onResume() {
        super.onResume()
    }

    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
