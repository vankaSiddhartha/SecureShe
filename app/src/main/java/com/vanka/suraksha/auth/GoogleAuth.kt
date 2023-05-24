package com.vanka.suraksha.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vanka.suraksha.MainActivity
import com.vanka.suraksha.R
import com.vanka.suraksha.databinding.ActivityGoogleAuthBinding

class GoogleAuth : AppCompatActivity() {
    private lateinit var binding:ActivityGoogleAuthBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGoogleAuthBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)
//        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100)
//            return
//        }
        binding.createUserPb.visibility = View.GONE



        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        firebaseAuth = Firebase.auth
        binding.googleSignBtn.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            googleSignInActivityResultLauncher.launch(intent)

        }


    }
    private val googleSignInActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {


                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = accountTask.getResult(ApiException::class.java)
                    firebaseAuthWithGoogleAccount(account)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show()
                }
            } else {

            }
        }
    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        binding.googeText.visibility = View.GONE
        binding.createUserPb.visibility = View.VISIBLE
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authRes ->
                if (authRes.additionalUserInfo!!.isNewUser) {

                    binding.createUserPb.visibility = View.GONE
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{

                    Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                }
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}