package com.vanka.suraksha.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vanka.suraksha.R
import com.vanka.suraksha.databinding.PostBinding
import com.vanka.suraksha.model.postModel
import com.vanka.suraksha.socialModule.CommentFragment

class PostAdapter(
    var context: Context,
    var list: ArrayList<postModel>,
    var supportFragmentManager: FragmentManager,
    position: Int
):RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    class ViewHolder(var binding: PostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PostBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

       holder.binding.likeButton.isChecked = false
        checkLike(
            list[position].postId,
            holder.binding.likeCountTextView,
            holder.binding.likeButton
        )
        checkComments(list[position].postId,holder)
        holder.binding.usernameTextView.text = list[position].postAuthor
        Glide.with(context).load(list[position].postAuthorImg).into(holder.binding.avatarImageView)
        Glide.with(context).load(list[position].postImage)
            .into(holder.binding.imagePreviewImageView)
        holder.binding.contentTextView.text = changeHashTagColor(list[position].postText.toString(),Color.BLUE)
        holder.binding.likeButton.setOnClickListener {

            like(list[position].postId, holder.binding.likeButton,holder,position)

        }
        holder.binding.commentButton.setOnClickListener {
         LoadFrag(CommentFragment(list[position].postId,position))
        }
    }

    private fun checkComments(postId: String?, holder: PostAdapter.ViewHolder) {
        var count =0
       FirebaseDatabase.getInstance().getReference("comment").child(postId.toString()).addValueEventListener(object :ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               for(data in snapshot.children){
                   count++
               }
               holder.binding.commentCountTextView.text = count.toString()
           }

           override fun onCancelled(error: DatabaseError) {

           }

       })
    }

    private fun LoadFrag(fragment: Fragment) {
        var load = supportFragmentManager.beginTransaction()
        load.replace(R.id.frameLayout,fragment)
        load.addToBackStack("CommentsFragment")
        load.commit()
    }

    private fun checkLike(postId: String?, likeCountTextView: TextView, likeButton: CheckBox) {
        var count =0
     FirebaseDatabase.getInstance().getReference("like").child(postId.toString()).addValueEventListener(object :ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
             for(data in snapshot.children){
                 count++
                 if(data.value!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                likeButton.isChecked = true
                 }
             }
             likeCountTextView.text = count.toString()
             count=0
         }

         override fun onCancelled(error: DatabaseError) {

         }

     })
    }

    private fun like(postId: String?, likeButton: CheckBox, holder: ViewHolder, position: Int) {

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

            val likeReference = FirebaseDatabase.getInstance().getReference("like").child(postId.toString())
                .child(userId)


            if (likeButton.isChecked) {

                likeReference.setValue(userId)
            } else {

                likeReference.removeValue()

            }
            checkLike(list[position].postId,holder.binding.likeCountTextView,holder.binding.likeButton)

    }
    fun changeHashTagColor(text: String, color: Int): SpannableString {
        val spannableString = SpannableString(text)
        val regex = Regex("#\\w+") // Regular expression pattern for hashtags
        val matches = regex.findAll(text)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            spannableString.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }
}