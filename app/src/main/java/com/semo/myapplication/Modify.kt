package com.semo.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
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
import java.util.regex.Pattern

class Modify: AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityModifyBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private lateinit var retrofit:Retrofit
    private lateinit var boardService: BoardService

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

        retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        boardService = retrofit.create(BoardService::class.java)

        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        post_id = intent.getIntExtra("post_id",0)
        user_id = intent.getStringExtra("user_id")

        binding.modifyTitle.setText(title)
        binding.modifyContent.setText(content)

        binding.confirm.setOnClickListener {
            title = binding.modifyTitle.text.toString()
            content = binding.modifyContent.text.toString()
            val titlePattern = "^.{1,25}$"
            var pattern = Pattern.compile(titlePattern)
            val contentPattern = "^.{1,99}$"
            var pattern2 = Pattern.compile(contentPattern)
            val titleMatcher = pattern.matcher(title)
            val contentMatcher = pattern2.matcher(content)
            if (titleMatcher.matches() && contentMatcher.matches() ) {
                boardService.modify(post_id!!,user_id, title, content).enqueue(object :
                    Callback<ModifyData> {
                    override fun onResponse(call: Call<ModifyData>, response: Response<ModifyData>) {
                        modyfiyData = response.body()
                        Log.d("modifydata",modyfiyData.toString())
                        if(modyfiyData?.code.equals("000")){
                            // 결과 돌려줄 인텐트 생성 후 저장
                            val returnIntent = Intent()
                            // 값 담기
                            returnIntent.putExtra("modifyTitle", modyfiyData?.content?.title)
                            returnIntent.putExtra("modifyContent",modyfiyData?.content?.content)
                            returnIntent.putExtra("modifyDate",modyfiyData?.content?.updated_date)
                            // 값 전달
                            // setResult() 첫번째 파라미터 - 상태값, 두번째 파라미터 - 전달하려는 인텐트
                            setResult(Activity.RESULT_OK,returnIntent)
                            // 최종 전달
                            finish()
                        }
                        else if(modyfiyData?.code.equals("001")){ //작가 불일치
                            toast(resources.getString(R.string.authormiss))
                        }
                    }
                    override fun onFailure(call: Call<ModifyData>, t: Throwable) {
                    }
                })
            } else if(!titleMatcher.matches()){
                toast(resources.getString(R.string.titlelong))
            } else if(!contentMatcher.matches()){
                toast(resources.getString(R.string.contentlong))
            }

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