package com.vanka.suraksha.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanka.suraksha.databinding.TrendinngItemBinding
import com.vanka.suraksha.model.HastagModelClass

class TrendingAdapter(var context: Context, var list: ArrayList<HastagModelClass>):RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {
    class ViewHolder(var binding:TrendinngItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TrendinngItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.trendingCount.text = list[position].count.toString()
        holder.binding.trendingHashtag.text="#"+list[position].name
        holder.binding.numberTextView.text = (position+1).toString()
    }
}