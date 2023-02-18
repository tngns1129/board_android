package com.semo.myapplication

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.semo.myapplication.databinding.ActivityPostBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

class Board : AppCompatActivity() {

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityPostBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    var post_id:Int? = null
    var author:String? = null
    var deleteData:DeleteData? = null

    var title: String? = null
    var content: String? = null
    var user_id:String? = null
    var modyfiyDate:String? = null

    var modyfiyData:ModifyData? = null
    var contentData:ContentViewData? = null
    var checkAuthorData:CheckAuthorData? = null

    var block_posts: String? = null
    var block_list =  ArrayList<Int>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var boardService: BoardService = retrofit.create(BoardService::class.java)

        mBinding = ActivityPostBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        binding.content.movementMethod = ScrollingMovementMethod.getInstance()
        title = intent.getStringExtra("title")

        sharedPreferences = getSharedPreferences("postBlockList", MODE_PRIVATE)
        var blocklist = sharedPreferences.getString("post_id","")

        if(!blocklist.isNullOrBlank()) {
            block_list =
                Gson().fromJson(blocklist, Array<Int>::class.java)
                    .toMutableList() as ArrayList<Int>
        }

        binding.title.setText(title)
        boardService.contentview(intent.getIntExtra("post_id",-1)).enqueue(object: Callback<ContentViewData> {
            override fun onFailure(call: Call<ContentViewData>, t: Throwable) {
            }
            override fun onResponse(call: Call<ContentViewData>, response: Response<ContentViewData>) {
                contentData = response.body()
                binding.content.setText(contentData!!.content.content)
                content = contentData!!.content.content
            }
        })
        author = intent.getStringExtra("author")
        modyfiyDate = intent.getStringExtra("updated_date")
        binding.author.setText(author)
        binding.date.setText(modyfiyDate)
        post_id = intent.getIntExtra("post_id",0)
        user_id = intent.getStringExtra("user_id")

        binding.delete.setOnClickListener {
            Log.d("delete",post_id.toString())
            Log.d("delete",user_id.toString())
            boardService.delete(post_id!!,user_id).enqueue(object: Callback<DeleteData> {
                override fun onFailure(call: Call<DeleteData>, t: Throwable) {
                }
                override fun onResponse(call: Call<DeleteData>, response: Response<DeleteData>) {
                    deleteData = response.body()
                    if(deleteData?.code.equals("000")) {
                        toast("삭제성공")
                        finish()
                    } else if(deleteData?.code.equals("001")) {
                        toast("작가 불일치")
                    }
                }
            })
        }
        binding.modify.setOnClickListener {
            val modifyIntent = Intent(this, Modify::class.java)
            modifyIntent.putExtra("title", title)
            modifyIntent.putExtra("content", content)
            modifyIntent.putExtra("post_id", post_id)
            modifyIntent.putExtra("user_id", user_id)
            startActivity(modifyIntent)
        }
        binding.report.setOnClickListener{
            var colorArray: Array<String> = arrayOf(
                resources.getString(R.string.report_list_1),
                resources.getString(R.string.report_list_2),
                resources.getString(R.string.report_list_3)) // 리스트에 들어갈 Array

            val builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.report))
                .setItems(colorArray,
                    DialogInterface.OnClickListener { dialog, which ->
                        // 여기서 인자 'which'는 배열의 position을 나타냅니다.
                        editor = sharedPreferences.edit()
                        if(which == 0) {
                            block_list.add(post_id!!)
                            block_posts = Gson().toJson(block_list)
                            editor.putString("post_id", block_posts)
                            editor.commit()
                        } else if(which ==1){
                            block_list.add(post_id!!)
                            block_posts = Gson().toJson(block_list)
                            editor.putString("post_id", block_posts)
                            editor.commit()
                        } else if(which ==2){
                            block_list.add(post_id!!)
                            block_posts = Gson().toJson(block_list)
                            editor.putString("post_id", block_posts)
                            editor.commit()
                        }
                    })
            // 다이얼로그를 띄워주기
            builder.show()

        }

    }

    override fun onResume() {
        super.onResume()
        var retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.server_adress))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var boardService: BoardService = retrofit.create(BoardService::class.java)
        boardService.checkauthor(post_id!!,user_id,).enqueue(object : Callback<CheckAuthorData>{
            override fun onResponse(call: Call<CheckAuthorData>, response: Response<CheckAuthorData>) {
                checkAuthorData = response.body()
                if(checkAuthorData?.code.equals("000")){
                    binding.modify.visibility = View.VISIBLE
                    binding.delete.visibility = View.VISIBLE
                }
                else if(checkAuthorData?.code.equals("001")){
                    binding.modify.visibility = View.GONE
                    binding.delete.visibility = View.GONE
                    toast(checkAuthorData?.msg.toString())
                } else{
                    binding.modify.visibility = View.GONE
                    binding.delete.visibility = View.GONE
                    toast(checkAuthorData?.msg.toString())
                }
            }

            override fun onFailure(call: Call<CheckAuthorData>, t: Throwable) {


            }

        })
        boardService.contentview(intent.getIntExtra("post_id",-1)).enqueue(object: Callback<ContentViewData> {
            override fun onFailure(call: Call<ContentViewData>, t: Throwable) {
            }
            override fun onResponse(call: Call<ContentViewData>, response: Response<ContentViewData>) {
                contentData = response.body()
                if(contentData != null) {
                    binding.content.setText(contentData!!.content.content)
                    content = contentData!!.content.content
                }
            }
        })
        binding.content.setText(content)
        binding.title.setText(title)
        binding.date.setText(modyfiyDate)

        binding.back.setOnClickListener {
            finish()
        }
    }


    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item1 -> {
                toast("Hi")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflatrer: MenuInflater = menuInflater
        inflatrer.inflate(R.menu.option_menu, menu)
        return true
    }
}
