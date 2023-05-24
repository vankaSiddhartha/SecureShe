package com.vanka.suraksha.socialModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vanka.suraksha.R
import com.vanka.suraksha.adapter.CommentAdapter
import com.vanka.suraksha.databinding.FragmentCommentBinding
import com.vanka.suraksha.model.CommentModel


class CommentFragment(var position: String?, var position1: Int) : Fragment() {
   private lateinit var binding:FragmentCommentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        getData()
        binding = FragmentCommentBinding.inflate(LayoutInflater.from(requireContext()))
        val manger = LinearLayoutManager(requireContext())
        manger.reverseLayout=true
        manger.stackFromEnd = true
        binding.commentsRecyclerView.layoutManager = manger
        binding.buttonBack.setOnClickListener {
            //LoadFrag(PostFragment(position1))
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.sendButton.setOnClickListener {
            sendData()
        }
        return binding.root
    }

    private fun sendData() {
        val data = CommentModel(FirebaseAuth.getInstance().currentUser!!.photoUrl.toString(),FirebaseAuth.getInstance().currentUser!!.displayName,binding.commentEditText.text.toString())
        FirebaseDatabase.getInstance().getReference("comment").child(position.toString()).push().setValue(data).addOnSuccessListener {
            Toast.makeText(requireContext(), "Successfully comment added!!", Toast.LENGTH_SHORT).show()
            binding.commentEditText.setText("")
            getData()
        }
    }


    private fun LoadFrag(fragment: Fragment) {
        var load = requireActivity().supportFragmentManager.beginTransaction()
        load.replace(R.id.frameLayout,fragment)
        load.commit()
    }
    fun getData(){
        val list = ArrayList<CommentModel>()
       FirebaseDatabase.getInstance().getReference("comment").child(position.toString()).addValueEventListener(object :ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               list.clear()
               for (data in snapshot.children){
                   var get_data = data.getValue(CommentModel::class.java)
                   list.add(get_data!!)
               }
               try {
                   binding.commentsRecyclerView.adapter = CommentAdapter(requireContext(),list)
               }catch (e:Exception){

               }

           }

           override fun onCancelled(error: DatabaseError) {

           }

       })
    }


}