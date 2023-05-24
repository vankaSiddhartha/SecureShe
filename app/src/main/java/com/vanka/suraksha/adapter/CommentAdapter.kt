package com.vanka.suraksha.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanka.suraksha.databinding.CommentIteamBinding
import com.vanka.suraksha.model.CommentModel

class CommentAdapter(val context: Context,var list:ArrayList<CommentModel>):RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    class ViewHolder(val binding:CommentIteamBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CommentIteamBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(list[position].profile).into(holder.binding.avatarImageView)
        holder.binding.usernameTextView.text = list[position].name
        holder.binding.commentContentTextView.text = list[position].text
    }
}