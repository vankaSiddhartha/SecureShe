package com.vanka.suraksha.gaurdian

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vanka.suraksha.databinding.FragmentAddGuardianBinding
import com.vanka.suraksha.model.GuardiansModel


class AddGuardian : Fragment() {

    private lateinit var binding: FragmentAddGuardianBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString("name", "default_value")

        binding = FragmentAddGuardianBinding.inflate(LayoutInflater.from(requireContext()))
        binding.button2.setOnClickListener {
            postGaurd(binding.editTextTextPersonName.text.toString())
        }
        binding.textView3.text = value
        return binding.root
    }

    private fun postGaurd(id: String) {
        val sharedPreferences = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        FirebaseDatabase.getInstance().getReference("guardians")
            .child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(GuardiansModel::class.java)
                    binding.textView3.text = data!!.name
                    editor.putString("id", data.id)
                    editor.putString("name",data.name)
                    editor.putString("uid",data.uid)
                    editor.apply()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


    }
}