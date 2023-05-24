package com.vanka.suraksha.socialModule

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vanka.suraksha.adapter.TrendingAdapter
import com.vanka.suraksha.databinding.FragmentTrendingAwaazBinding
import com.vanka.suraksha.model.HastagModelClass
import java.time.LocalDate


class TrendingAwaaz : Fragment() {
private lateinit var binding:FragmentTrendingAwaazBinding



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrendingAwaazBinding.inflate(LayoutInflater.from(requireContext()),container, false)
        binding.trendingRecyclerView.addItemDecoration(DividerItemDecoration(binding.trendingRecyclerView.context, DividerItemDecoration.VERTICAL))
       val manger =  LinearLayoutManager(requireContext())
       // manger.reverseLayout=true
        //manger.stackFromEnd = true
        binding.trendingRecyclerView.layoutManager = manger


        showTrending()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTrending() {
        val list = ArrayList<HastagModelClass>()
        val today = LocalDate.now()
        val formattedToday = today.toString()
        val dB =  FirebaseDatabase.getInstance().getReference("tags").child(formattedToday)
        dB.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children){

                        val currentCount = data.child("count").getValue(Int::class.java)
                        val name = data.child("name").value
                        list.add(HastagModelClass(name.toString(),currentCount!!.toInt()))
                    }
                    try {

                        binding.trendingRecyclerView.adapter = TrendingAdapter(requireContext(),
                            list
                        )
                    }catch (e:Exception){

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


}