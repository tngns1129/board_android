package com.semo.myapplication

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.os.UserManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.semo.myapplication.databinding.ActivityPostListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BoardList : AppCompatActivity() {

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

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
        val user = intent.getSerializableExtra("user") as UserData
        listAdapter = user?.let { BoardListAdapter(itemList, it) }!!

        sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // 레이아웃 매니저와 어댑터 설정
        binding.postlist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.postlist.adapter = listAdapter


        binding.writing.setOnClickListener {
            val writingIntent = Intent(this, Writing::class.java)
            writingIntent.putExtra("user", user)
            startActivity(writingIntent)
        }

        binding.back.setOnClickListener{

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("")
                .setMessage(R.string.exit_info)
                .setPositiveButton(R.string.confirm,
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        finish()
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialogInterface, i ->
                    })
                .setNeutralButton(R.string.signout,
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        editor.putString("id","")
                        editor.putString("pw","")
                        editor.commit()
                        val signinIntent = Intent(this, Signin::class.java)
                        startActivity(signinIntent)
                        finish()
                    })
            dialog.show()
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
                for (i in post!!.reversed()) {
                    itemList.add(TitleViewData(i.title,i.brief_description,i.updated_date,i.user,i.id))
                }
                binding.postlist.layoutManager = LinearLayoutManager(this@BoardList, LinearLayoutManager.VERTICAL, false)
                binding.postlist.adapter = listAdapter
            }
        })
    }
    var mBackWait:Long = 0
    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Snackbar.make(binding.root,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show()
        } else {
            finish() //액티비티 종료
        }
    }
    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
    }

}