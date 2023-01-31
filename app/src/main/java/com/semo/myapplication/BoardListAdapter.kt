package com.semo.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.semo.myapplication.databinding.ActivityPostListBinding

class BoardListAdapter(
    val contents: ArrayList<TitleViewData>,
    val user:User
) : RecyclerView.Adapter<BoardListAdapter.PostViewHolder>() {

    private lateinit var binding: ActivityPostListBinding

    class PostViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.post_title)
        val contents: TextView = itemView.findViewById(R.id.post_content)
        val author: TextView = itemView.findViewById(R.id.post_author)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_post_list, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.title.text = contents[position].title
        holder.contents.text = contents[position].brief_description
        holder.author.text = contents[position].user?.username

        holder.itemView.setOnClickListener(){
            val intent = Intent(holder.itemView?.context, Board::class.java)
            intent.putExtra("title", holder.title.text.toString())
            intent.putExtra("brief_description", contents[position].brief_description)
            intent.putExtra("updated_date", contents[position].updated_date)
            intent.putExtra("author", contents[position].user?.username)
            intent.putExtra("post_id", contents[position].id)
            intent.putExtra("user_id",user.id)
            ContextCompat.startActivities(holder.itemView.context, arrayOf(intent), null)
        }

    }

    override fun getItemCount(): Int {
        return contents.size
    }

}