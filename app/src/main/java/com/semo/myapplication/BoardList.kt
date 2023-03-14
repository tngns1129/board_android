package com.semo.myapplication

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.semo.myapplication.databinding.ActivityPostListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class BoardList : AppCompatActivity() {

    private lateinit var shared_login : SharedPreferences
    private lateinit var shared_block : SharedPreferences
    private lateinit var login_editor : SharedPreferences.Editor
    private lateinit var block_editor : SharedPreferences.Editor
    var block_list =  ArrayList<Int>();

    private lateinit var retrofit : Retrofit
    private lateinit var boardService: BoardService

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityPostListBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    val itemList = arrayListOf<BriefContentData>()      // 아이템 배열

       // 어댑터
    lateinit var listAdapter:BoardListAdapter
    var post:BriefContentViewData? = null

    var title:String? = null
    var author:String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPostListBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        boardService = retrofit.create(BoardService::class.java)

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        val user = intent.getSerializableExtra("user") as UserData
        listAdapter = user?.let { BoardListAdapter(itemList, it) }!!
        shared_login = getSharedPreferences("loginInfo", MODE_PRIVATE)
        login_editor = shared_login.edit()

        shared_block = getSharedPreferences("postBlockList", MODE_PRIVATE)
        val blocklist = shared_block.getString("post_id","")
        if(!blocklist.isNullOrBlank()) {
            block_list =
                Gson().fromJson(blocklist, Array<Int>::class.java)
                    .toMutableList() as ArrayList<Int>
        }else{
            block_list.clear()
        }

        boardService.titleview().enqueue(object: Callback<BriefContentViewData> {
            override fun onFailure(call: Call<BriefContentViewData>, t: Throwable) {
                Log.d("boardsss",t.toString())
            }

            override fun onResponse(call: Call<BriefContentViewData>, response: Response<BriefContentViewData>) {
                Log.d("boardsss","body : "+ response.body())
                post = response.body()
                itemList.clear()
                var t:String
                if(post?.code == "000") {
                    for (i in post?.content!!) {
                        if(i.title?.length!! >=25) {
                            t = i.title!!.substring(0 until 25) + "..."
                            itemList.add(
                                BriefContentData(
                                    t,
                                    i.brief_description,
                                    i.updated_date?.toDate()?.formatTo("MM/dd HH:mm"),
                                    i.user,
                                    i.id,
                                    i.comment_count
                                )
                            )
                        } else{
                            itemList.add(
                                BriefContentData(
                                    i.title,
                                    i.brief_description,
                                    i.updated_date?.toDate()?.formatTo("MM/dd HH:mm"),
                                    i.user,
                                    i.id,
                                    i.comment_count
                                )
                            )
                        }
                    }
                    val delete = arrayListOf<BriefContentData>()
                    for (i in itemList!!) {
                        for (k in block_list.distinct()) {
                            if (i.id == k) {
                                delete.add(i)
                            }
                        }
                    }
                    itemList.removeAll(delete)
                }
                Log.d("block",block_list.toString())
            }
        })

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
                        login_editor.putString("id","")
                        login_editor.putString("pw","")
                        login_editor.commit()
                        val signinIntent = Intent(this, Signin::class.java)
                        startActivity(signinIntent)
                        finish()
                    })
            dialog.show()
        }

        binding.refresh.setOnRefreshListener {
            shared_block = getSharedPreferences("postBlockList", MODE_PRIVATE)
            val blocklist = shared_block.getString("post_id","")
            if(!blocklist.isNullOrBlank()) {
                block_list =
                    Gson().fromJson(blocklist, Array<Int>::class.java)
                        .toMutableList() as ArrayList<Int>
            }else{
                block_list.clear()
            }
            boardService.titleview().enqueue(object: Callback<BriefContentViewData> {
                override fun onFailure(call: Call<BriefContentViewData>, t: Throwable) {
                    Log.d("boardsss",t.toString())
                }

                override fun onResponse(call: Call<BriefContentViewData>, response: Response<BriefContentViewData>) {
                    Log.d("boardsss","body : "+ response.body())
                    post = response.body()
                    itemList.clear()
                    var t:String
                    if(post?.code == "000") {
                        for (i in post?.content!!) {
                            if(i.title?.length!! >=25) {
                                t = i.title!!.substring(0 until 25) + "..."
                                itemList.add(
                                    BriefContentData(
                                        t,
                                        i.brief_description,
                                        i.updated_date?.toDate()?.formatTo("MM/dd HH:mm"),
                                        i.user,
                                        i.id,
                                        i.comment_count
                                    )
                                )
                            } else{
                                itemList.add(
                                    BriefContentData(
                                        i.title,
                                        i.brief_description,
                                        i.updated_date?.toDate()?.formatTo("MM/dd HH:mm"),
                                        i.user,
                                        i.id,
                                        i.comment_count
                                    )
                                )
                            }
                        }
                        val delete = arrayListOf<BriefContentData>()
                        for (i in itemList!!) {
                            for (k in block_list.distinct()) {
                                if (i.id == k) {
                                    delete.add(i)
                                }
                            }
                        }
                        itemList.removeAll(delete)
                    }
                    Log.d("block",block_list.toString())
                    binding.postlist.layoutManager = LinearLayoutManager(this@BoardList, LinearLayoutManager.VERTICAL, false)
                    binding.postlist.adapter = listAdapter
                }
            })
            binding.refresh.isRefreshing = false
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

    }
    var mBackWait:Long = 0
    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Snackbar.make(binding.root,resources.getString(R.string.backpress), Snackbar.LENGTH_LONG).show()
        } else {
            finish() //액티비티 종료
        }
    }
    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
    }
    fun String.toDate(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

}


