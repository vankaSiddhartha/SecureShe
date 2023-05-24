package com.vanka.suraksha.socialModule

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vanka.suraksha.MainActivity
import com.vanka.suraksha.databinding.ActivityUploadPostBinding
import com.vanka.suraksha.model.HastagModelClass
import com.vanka.suraksha.model.postModel
import java.io.FileNotFoundException
import java.time.LocalDate
import java.util.*

class UploadPost : AppCompatActivity() {
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadImageToFirebase(it)
        }
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var binding:ActivityUploadPostBinding
    private lateinit var imgUrl:Uri

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.progressBar.visibility = View.GONE

        binding.buttonBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        binding.buttonAddImage.setOnClickListener {
            selectImageFromGallery()
        }
        binding.buttonTweet.setOnClickListener {
            Toast.makeText(this, "ol", Toast.LENGTH_SHORT).show()
            if (binding.editTextCaption.text.toString().isNotEmpty()||imgUrl.toString().isNotEmpty()) {
                               uploadData(binding.editTextCaption.text.toString(), imgUrl)
            }else{
                Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadData(awwaz: String, imgUrl: Uri) {
       val data = postModel(UUID.randomUUID().toString(),imgUrl.toString(),awwaz,FirebaseAuth.getInstance().currentUser?.displayName.toString(),FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),FirebaseAuth.getInstance().currentUser?.uid)
   FirebaseDatabase.getInstance().getReference("post").push().setValue(data).addOnSuccessListener {
      var list = extractHashtagsFromText(awwaz)
       try {
           // Code to access the file or directory
           firebaseTagCall(list)
       } catch (e: FileNotFoundException) {
           // Handle the exception (e.g., log an error, show a message to the user)
       }

     //
   }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun firebaseTagCall(q: List<String>){
        val today = LocalDate.now()
        val formattedToday = today.toString()
        val databaseRef=  FirebaseDatabase.getInstance().getReference("tags").child(formattedToday)
        for ((index, value) in q.withIndex()){
          var kry =  value.replace("#", "")
          databaseRef.child(kry).addListenerForSingleValueEvent(object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                  val currentCount = snapshot.child("count").getValue(Int::class.java)
                  val name = snapshot.child("name")
                  var count = currentCount?:0
                  count += 1
                  databaseRef.child(kry).setValue(HastagModelClass(kry,count))


              }

              override fun onCancelled(error: DatabaseError) {
                  // Handle error
              }
          })

        }
        startActivity(Intent(this,MainActivity::class.java))
    }
    fun extractHashtagsFromText(text: String): List<String> {
        val regex = Regex("#\\w+") // Regular expression pattern for hashtags
        val matches = regex.findAll(text)
        return matches.map { it.value }.toList()
    }

    private fun selectImageFromGallery() {
        selectImageLauncher.launch("image/*")
    }
    private fun uploadImageToFirebase(imageUri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val storageRef = storageReference.child("images/$userId/${UUID.randomUUID()}")

            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener {
                        imgUrl = it
                        binding.progressBar.visibility = View.GONE
                        Glide.with(this).load(imageUri).into(binding.imageViewPreview)
                    }
                }

                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }


}