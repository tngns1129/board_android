package com.semo.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.semo.myapplication.databinding.ActivityPostBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Board : AppCompatActivity() {

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

    var modyfiyData:ModyfiyData? = null
    var contentData:ContentViewData? = null
    var checkAuthorData:CheckAuthorData? = null

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

        binding.title.setText(intent.getStringExtra("title"))
        boardService.contentview(intent.getIntExtra("post_id",-1)).enqueue(object: Callback<ContentViewData> {
            override fun onFailure(call: Call<ContentViewData>, t: Throwable) {
            }
            override fun onResponse(call: Call<ContentViewData>, response: Response<ContentViewData>) {
                contentData = response.body()
                binding.content.setText(contentData!!.content.toString())
                content = contentData!!.content.toString()
            }
        })
        author = intent.getStringExtra("author")
        binding.author.setText(author)
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
                    deleteData?.let { it1 -> toast(it1.msg) }
                    if(deleteData?.code.equals("000"))
                        finish()
                }
            })
        }
        binding.modify.setOnClickListener {
            boardService.checkauthor(post_id!!,user_id,).enqueue(object : Callback<CheckAuthorData>{
                override fun onResponse(call: Call<CheckAuthorData>, response: Response<CheckAuthorData>) {
                    checkAuthorData = response.body()
                    if(checkAuthorData?.code.equals("000")){
                        binding.title.visibility = View.INVISIBLE
                        binding.content.visibility = View.INVISIBLE
                        binding.delete.visibility = View.INVISIBLE
                        binding.modify.visibility = View.INVISIBLE
                        binding.modifyTitle.visibility = View.VISIBLE
                        binding.modifyContent.visibility = View.VISIBLE
                        binding.modifyConfirm.visibility = View.VISIBLE
                        binding.modifyCancel.visibility = View.VISIBLE
                        binding.modifyTitle.setText(binding.title.text)
                        binding.modifyContent.setText(binding.content.text)
                    }
                    else if(checkAuthorData?.code.equals("001")){
                        checkAuthorData?.msg?.let { it1 -> toast(it1) }
                    }
                }

                override fun onFailure(call: Call<CheckAuthorData>, t: Throwable) {


                }

            })

        }
        binding.modifyCancel.setOnClickListener {
            binding.title.visibility = View.VISIBLE
            binding.content.visibility = View.VISIBLE
            binding.delete.visibility = View.VISIBLE
            binding.modify.visibility = View.VISIBLE
            binding.modifyTitle.visibility = View.INVISIBLE
            binding.modifyContent.visibility = View.INVISIBLE
            binding.modifyConfirm.visibility = View.INVISIBLE
            binding.modifyCancel.visibility = View.INVISIBLE
        }
        binding.modifyConfirm.setOnClickListener {
            title = binding.modifyTitle.text.toString()
            content = binding.modifyContent.text.toString()
            boardService.modify(post_id!!,user_id, title, content).enqueue(object : Callback<ModyfiyData>{
                override fun onResponse(call: Call<ModyfiyData>, response: Response<ModyfiyData>) {
                    modyfiyData = response.body()
                    if(modyfiyData?.code.equals("000")){
                        binding.title.visibility = View.VISIBLE
                        binding.content.visibility = View.VISIBLE
                        binding.delete.visibility = View.VISIBLE
                        binding.modify.visibility = View.VISIBLE
                        binding.modifyTitle.visibility = View.INVISIBLE
                        binding.modifyContent.visibility = View.INVISIBLE
                        binding.modifyConfirm.visibility = View.INVISIBLE
                        binding.modifyCancel.visibility = View.INVISIBLE
                        binding.title.setText(title)
                        binding.content.setText(content)
                    }
                    else if(modyfiyData?.code.equals("001")){
                        modyfiyData?.let { it1 -> toast(it1.msg) }
                    }
                }

                override fun onFailure(call: Call<ModyfiyData>, t: Throwable) {


                }

            })
        }
    }
    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
