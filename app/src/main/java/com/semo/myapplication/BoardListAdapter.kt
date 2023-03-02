package com.semo.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.semo.myapplication.databinding.ActivityPostListBinding

class BoardListAdapter(
    val contents: ArrayList<BriefContentData>,
    val user:UserData,
) : RecyclerView.Adapter<BoardListAdapter.PostViewHolder>() {

    private lateinit var binding: ActivityPostListBinding

    class PostViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.post_title)
        val contents: TextView = itemView.findViewById(R.id.post_content)
        val author: TextView = itemView.findViewById(R.id.post_author)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_post_list, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.title.text = contents[position].title + "     [" + contents[position].comment_count + "]"
        holder.contents.text = contents[position].brief_description
        holder.author.text = contents[position].user.username
        var d:String
        var t:String
        var date:String

        if(contents[position].updated_date.toString().substring(5 until 6).equals("0")) // 월 10의자리
            d = contents[position].updated_date.toString().substring(6 until 7)     //월 1자리 입력
        else
            d = contents[position].updated_date.toString().substring(5 until 7)     //월 2자리 입력

        d = d + "/" + contents[position].updated_date.toString().substring(8 until 10)

        Log.d("date", contents[position].updated_date.toString())

        t = contents[position].updated_date.toString().substring(11 until 16)
        date = d+ " "+t
        holder.date.text = date

        holder.itemView.setOnClickListener(){
            val intent = Intent(holder.itemView?.context, Board::class.java)
            intent.putExtra("title", contents[position].title)
            intent.putExtra("brief_description", contents[position].brief_description)
            intent.putExtra("updated_date", date)
            intent.putExtra("author", contents[position].user?.username)
            intent.putExtra("post_id", contents[position].id)
            intent.putExtra("user_id",user.id)
            intent.putExtra("user", user)
            ContextCompat.startActivities(holder.itemView.context, arrayOf(intent), null)
        }

    }

    override fun getItemCount(): Int {
        return contents.size
    }

}