package com.vanka.suraksha.socialModule

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vanka.suraksha.adapter.PostAdapter
import com.vanka.suraksha.databinding.FragmentPostBinding
import com.vanka.suraksha.model.postModel

class PostFragment(var position: Int) : Fragment() {



private lateinit var binding:FragmentPostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(LayoutInflater.from(requireContext()))
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(),UploadPost::class.java))
        }

        binding.recyclerView.addItemDecoration(DividerItemDecoration(binding.recyclerView.context, DividerItemDecoration.VERTICAL))
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getData()

        return binding.root
    }

    private fun getData() {
        val list = ArrayList<postModel>()
        FirebaseDatabase.getInstance().getReference("post").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (data in snapshot.children){
                    var get_data = data.getValue(postModel::class.java)
                    list.add(get_data!!)
                }
                try {
                    list.shuffle()
                    binding.recyclerView.adapter = PostAdapter(requireContext(),list,requireActivity().supportFragmentManager,position)
                }catch (e:Exception){

                }

              //  LinearLayoutManager(requireContext()).scrollToPosition(position)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



}