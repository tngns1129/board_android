package com.example.myapplication

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignupBinding
import com.example.myapplication.databinding.ActivityWritingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Writing : AppCompatActivity () {
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityWritingBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    var writing:WritingData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWritingBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        val intent = intent
        val user = intent.getSerializableExtra("user") as User

        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var writingService: WritingService = retrofit.create(WritingService::class.java)

        var title: String? = null
        var contents: String? = null
        var author: String? = null

        binding.confirm.setOnClickListener {
            title = binding.title.text.toString()
            contents = binding.contents.text.toString()
            author = user.id
            Log.d("POST", "title :"+title)
            Log.d("POST", "contents :"+contents)
            Log.d("POST", "uid :"+author)
            writingService.requestPost(title,contents,author).enqueue(object: Callback<WritingData> {
                override fun onFailure(call: Call<WritingData>, t: Throwable) {
                }
                override fun onResponse(call: Call<WritingData>, response: Response<WritingData>) {
                    writing = response.body()
                    Log.d("POST","msg : "+writing?.msg)
                    Log.d("POST","code : "+writing?.code)
                    writing?.let { it1 -> toast(it1.msg) }
                    if(writing?.code.equals("000")){
                        finish()
                    }
                }
            })
        }

        binding.cancel.setOnClickListener {

        }

    }
    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}