package com.semo.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentListAdapter (
    val contents: ArrayList<CommentData>,
    val user:UserData
    ) : RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>(){

    class CommentViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var comment: TextView = itemView.findViewById(R.id.comment)
        val author: TextView = itemView.findViewById(R.id.post_author)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_comment_list, parent, false)
        return CommentListAdapter.CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        var d:String
        var t:String
        var date:String

        if(contents[position].updated_date.toString().length>15) {
            if (contents[position].updated_date.toString().substring(5 until 6)
                    .equals("0")
            ) // 월 10의자리
                d = contents[position].updated_date.toString().substring(6 until 7)     //월 1자리 입력
            else
                d = contents[position].updated_date.toString().substring(5 until 7)     //월 2자리 입력

            d = d + "/" + contents[position].updated_date.toString().substring(8 until 10)

            Log.d("date", contents[position].updated_date.toString())

            t = contents[position].updated_date.toString().substring(11 until 16)
            date = d + " " + t
        } else{
            date = contents[position].updated_date.toString()
        }
        holder.comment.text = contents[position].content
        holder.author.text = contents[position].user?.username
        holder.date.text = date


        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}