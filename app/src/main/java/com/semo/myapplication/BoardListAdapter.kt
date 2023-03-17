package com.semo.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.semo.myapplication.databinding.ActivityPostListBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

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
        val post_comment_number: TextView = itemView.findViewById(R.id.post_comment_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_post_list, parent, false)
        return PostViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.title.text = contents[position].title //+ "     [" + contents[position].comment_count + "]"
        holder.post_comment_number.text =" : "+contents[position].comment_count
        holder.contents.text = contents[position].brief_description
        holder.author.text = contents[position].user.username
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd")
        val formatted = current.format(formatter)
        if(formatted == contents[position].updated_date?.toDate()?.formatTo("dd")){
            holder.date.text = contents[position].updated_date?.toDate()?.formatTo("HH:mm")
        } else{
            holder.date.text = contents[position].updated_date?.toDate()?.formatTo("MM/dd")
        }



        holder.itemView.setOnClickListener(){
            val intent = Intent(holder.itemView?.context, Board::class.java)
            intent.putExtra("updated_date", contents[position].updated_date)
            intent.putExtra("author", contents[position].user?.username)
            intent.putExtra("post_id", contents[position].id)
            intent.putExtra("user_id",user.id)
            ContextCompat.startActivities(holder.itemView.context, arrayOf(intent), null)
        }

    }

    fun String.toDate(dateFormat: String = "MM/dd HH:mm", timeZone: TimeZone = TimeZone.getDefault()): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

    override fun getItemCount(): Int {
        return contents.size
    }

}