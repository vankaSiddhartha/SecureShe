package com.vanka.suraksha.socialModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.vanka.suraksha.R
import com.vanka.suraksha.databinding.ActivityCommentBinding

class CommentActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCommentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.buttonBack.setOnClickListener {
            LoadFrag(PostFragment(0))
        }

    }
    private fun LoadFrag(fragment: Fragment) {
        var load = supportFragmentManager.beginTransaction()
        load.replace(R.id.frameLayout,fragment)
        load.commit()
    }
}