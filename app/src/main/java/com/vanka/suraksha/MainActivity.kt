package com.vanka.suraksha

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.vanka.suraksha.databinding.ActivityMainBinding
import com.vanka.suraksha.emergencyButton.EmergencyButtonFragment
import com.vanka.suraksha.gaurdian.AddGuardian
import com.vanka.suraksha.map.MapFragment
import com.vanka.suraksha.services.CheckLocationInBackground
import com.vanka.suraksha.services.ShakeService
import com.vanka.suraksha.socialModule.PostFragment
import com.vanka.suraksha.socialModule.TrendingAwaaz


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
   private val permissionRequestCode = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val serviceIntent = Intent(this, CheckLocationInBackground ::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        val serviceIntent1 = Intent(this, ShakeService::class.java)
      startService(serviceIntent1)



        supportActionBar?.hide()

        LoadFrag(EmergencyButtonFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.alertBtn->{
                    LoadFrag(EmergencyButtonFragment())
                    true
                }
                R.id.Map->{
                    LoadFrag(MapFragment())
                    true
                }
                R.id.awaz->{
                    LoadFrag(PostFragment(0))
                    true
                }
                R.id.trending->{
                    LoadFrag(TrendingAwaaz())
                    true
                }
                R.id.setting->{
                    LoadFrag(AddGuardian())
                    true
                }

                else -> {
                    LoadFrag(EmergencyButtonFragment())
                    true
                }
            }
            }
        }
    private fun LoadFrag(fragment: Fragment) {
        var load = supportFragmentManager.beginTransaction()
        load.replace(R.id.frameLayout,fragment)
        load.commit()
    }

}
