package com.semo.myapplication

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.semo.myapplication.databinding.ActivityModifyBinding
import com.semo.myapplication.databinding.ActivityWritingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Modify: AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityModifyBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    var modyfiyData:ModifyData? = null

    var title: String? = null
    var content: String? = null
    var post_id:Int? = null
    var user_id:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityModifyBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var boardService: BoardService = retrofit.create(BoardService::class.java)



        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        post_id = intent.getIntExtra("post_id",0)
        user_id = intent.getStringExtra("user_id")

        binding.modifyTitle.setText(title)
        binding.modifyContent.setText(content)

        binding.confirm.setOnClickListener {
            title = binding.modifyTitle.text.toString()
            content = binding.modifyContent.text.toString()
            boardService.modify(post_id!!,user_id, title, content).enqueue(object :
                Callback<ModifyData> {
                override fun onResponse(call: Call<ModifyData>, response: Response<ModifyData>) {
                    modyfiyData = response.body()
                    if(modyfiyData?.code.equals("000")){
                        finish()
                    }
                    else if(modyfiyData?.code.equals("001")){ //작가 불일치
                        toast("작가 불일치")
                    }
                }
                override fun onFailure(call: Call<ModifyData>, t: Throwable) {
                }
            })
        }

        binding.cancel.setOnClickListener {
            finish()
        }

        binding.back.setOnClickListener{
            finish()
        }

    }

    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}