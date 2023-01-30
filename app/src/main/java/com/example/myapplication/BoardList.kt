package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityPostListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BoardList : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityPostListBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    val itemList = arrayListOf<TitleViewData>()      // 아이템 배열

       // 어댑터
    lateinit var listAdapter:BoardListAdapter
    var post:List<TitleViewData>? = null

    var title:String? = null
    var author:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPostListBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        val user = intent.getSerializableExtra("user") as User?
        listAdapter = user?.let { BoardListAdapter(itemList, it) }!!


        // 레이아웃 매니저와 어댑터 설정
        binding.postlist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.postlist.adapter = listAdapter


        binding.writing.setOnClickListener {
            val writingIntent = Intent(this, Writing::class.java)
            writingIntent.putExtra("user", user)
            startActivity(writingIntent)
        }


        // 아이템 추가
        /*
        itemList.add(PostData("Ada", "010-1234-5678"))
        itemList.add(PostData("James", "010-1234-5678"))
        itemList.add(PostData("John", "010-1234-5678"))
        itemList.add(PostData("Cena", "010-1234-5678"))
        itemList.add(PostData("Ada", "010-1234-5678"))
        itemList.add(PostData("James", "010-1234-5678"))
        itemList.add(PostData("John", "010-1234-5678"))
        itemList.add(PostData("Cena", "010-1234-5678"))
        itemList.add(PostData("Ada", "010-1234-5678"))
        itemList.add(PostData("James", "010-1234-5678"))
        itemList.add(PostData("John", "010-1234-5678"))
        itemList.add(PostData("Cena", "010-1234-5678"))
        itemList.add(PostData("Ada", "010-1234-5678"))
        itemList.add(PostData("James", "010-1234-5678"))
        itemList.add(PostData("John", "010-1234-5678"))
        itemList.add(PostData("Cena", "010-1234-5678"))
        */


    }

    override fun onResume() {
        Log.d("ListResume", "Hi")
        super.onResume()
        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var boardService: BoardService = retrofit.create(BoardService::class.java)

        boardService.titleview().enqueue(object: Callback<List<TitleViewData>> {
            override fun onFailure(call: Call<List<TitleViewData>>, t: Throwable) {
                Log.d("board",t.toString())
            }

            override fun onResponse(call: Call<List<TitleViewData>>, response: Response<List<TitleViewData>>) {
                Log.d("board","body : "+ response.body())
                post = response.body()
                itemList.clear()
                for (i in post!!) {
                    itemList.add(TitleViewData(i.title,i.brief_description,i.updated_date,i.user,i.id))
                }
                binding.postlist.layoutManager = LinearLayoutManager(this@BoardList, LinearLayoutManager.VERTICAL, false)
                binding.postlist.adapter = listAdapter
            }
        })
    }

    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
    }
}