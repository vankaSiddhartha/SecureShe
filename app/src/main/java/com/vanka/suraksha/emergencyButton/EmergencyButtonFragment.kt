package com.vanka.suraksha.emergencyButton

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.vanka.suraksha.databinding.FragmentEmergencyButtonBinding
import com.vanka.suraksha.model.NotificationData
import com.vanka.suraksha.model.PushNotification
import com.vanka.suraksha.notifications.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EmergencyButtonFragment : Fragment() {
 private lateinit var binding:FragmentEmergencyButtonBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmergencyButtonBinding.inflate(LayoutInflater.from(requireContext()),container,false)
        binding.content.startRippleAnimation()
        binding.button.setOnClickListener {
           askPhonePermission()
        }
        return binding.root
    }

    fun askPhonePermission(){

            readExternalPermissionContract.launch(android.Manifest.permission.CALL_PHONE)

    }
    private val readExternalPermissionContract =  registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermissionAccepted ->
        if(isPermissionAccepted) {

            makeCall()

        } else {
            Toast.makeText(requireContext(), "Permission is declined", Toast.LENGTH_SHORT).show()


        }
    }

    private fun makeCall() {
        val sharedPreferences = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString("id", "e35s3_dZR3qbuqjGTSK4gp:APA91bG1hpgPa8my9QdRswVYnOf3h0G7FgPAhpaCTz4dOXcT1ys6FHi8xVcMhkLy7nCweonBf1nH8cPHgSLVdSH2bVHhjpzVJjFary7xMU55HSLntCODhWdFvyJS_DVjiCfM14Ajr7rA")
        Toast.makeText(requireContext(), "Call", Toast.LENGTH_SHORT).show()
        PushNotification(
            NotificationData("Alert", FirebaseAuth.getInstance().currentUser!!.displayName.toString()+" is at emergency"),
            value.toString()
        ).also {


            sendNotification(it)
        }

    }
//    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val response = RetrofitInstance.api.postNotification(notification)
//            if(response.isSuccessful) {
//                Toast.makeText(requireContext(), "Sucessfull", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(requireContext(), "noo", Toast.LENGTH_SHORT).show()
//            }
//        } catch(e: Exception) {
//            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
//        }
//    }
private fun sendNotification(notification: PushNotification) {
    val context = requireContext()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    val phoneNumber = "1234567890" // Replace with the desired phone number
                    val dialIntent = Intent(Intent.ACTION_CALL)
                    dialIntent.data = Uri.parse("tel:$phoneNumber")
                    startActivity(dialIntent)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}










}