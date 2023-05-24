package com.vanka.suraksha.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.vanka.suraksha.R
import com.vanka.suraksha.databinding.FragmentMapBinding
import com.vanka.suraksha.model.LocationModel
import com.vanka.suraksha.services.CheckLocationInBackground
import com.vanka.suraksha.services.ShakeService


class MapFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private  var anotationApi:AnnotationPlugin?=null
    lateinit var annotationConfig:AnnotationConfig
    var pointAnnotationManger:PointAnnotationManager?=null


    private lateinit var binding: FragmentMapBinding
    private var mapView: MapView? = null
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val permissionRequestCode = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (isLocationPermissionGranted()) {
            // Location permission is already granted
            // Your code for accessing location or other related operations
            val serviceIntent = Intent(requireContext(), CheckLocationInBackground ::class.java)
            ContextCompat.startForegroundService(requireContext(), serviceIntent)
            val serviceIntent1 = Intent(requireContext(), ShakeService::class.java)
            requireContext().startService(serviceIntent1)
        } else {
            // Request location permissions
            requestLocationPermissions()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding =
            FragmentMapBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        mapView = binding.mapView
        //  mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.DARK ,
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    getLoc()
                    anotationApi = mapView?.annotations
                    annotationConfig = AnnotationConfig(
                        layerId = "map_annotation"
                    )
                    pointAnnotationManger = anotationApi?.createPointAnnotationManager(annotationConfig)

                }
            }
        )








        return binding.root
    }

    private fun addLocationToMap(latitude: Double, longitude: Double) {
        mapView!!.getMapboxMap().setCamera(
            CameraOptions.Builder().center(Point.fromLngLat(longitude, latitude))
                .zoom(16.0)
                .build()
        )
        createMapMark(latitude,longitude)
    }


    private fun getLoc(){
        if (ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100)
            return
        }
        val location = fusedLocationClient.lastLocation
 try {
     location.addOnSuccessListener {it->


         addLocationToMap(it.latitude,it.longitude)



     }
 }catch (e:Exception){

 }





    }
    private fun createMapMark(latitude: Double, longitude: Double) {
        val sharedPreferences = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString("uid", "default_value")
        val bitmap = covertImageToBit(AppCompatResources.getDrawable(requireContext(),R.drawable.red_marker))
        val pointAnnotationOptions:PointAnnotationOptions=PointAnnotationOptions()
            .withPoint(Point.fromLngLat(longitude,latitude))
            .withIconImage(bitmap)
     pointAnnotationManger?.create(pointAnnotationOptions)
        checkDangerArea(latitude,longitude)
        try {
            FirebaseDatabase.getInstance().getReference("myLoc").child(value.toString()).setValue(LocationModel(latitude,longitude))
        }catch (e:Exception){

        }

    }
    private fun covertImageToBit(source:Drawable?):Bitmap{

        return if (source is BitmapDrawable){
            source.bitmap
        }else{
            val constState = source?.constantState
            val drawable = constState?.newDrawable()?.mutate()
            val bitmap:Bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,drawable!!.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0,0,canvas.width,canvas.height)
            drawable.draw(canvas)
            bitmap
        }

    }
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371 // Radius of the Earth in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }
    fun checkDangerArea(latitude: Double,longitude: Double){
        val locationA = arrayOf(
            Pair(18.288002, 83.891958), // Srikakulam Collectorate
            Pair(18.304981, 83.895118), // Srikakulam Railway Station
            Pair(18.298537, 83.898674), // Srikakulam Bus Stand
            Pair(18.304524, 83.891512), // Srikakulam Beach
            Pair(18.291556, 83.894678), // Ambedkar Circle
            Pair(18.288685, 83.897509), // Jetti Subbarao College
            Pair(18.289825, 83.891877), // LIC Building
            Pair(18.291250, 83.892648), // District Court, Srikakulam
            Pair(18.295917, 83.948768), // Srikurmam Temple
            Pair(18.315674, 83.876079),// LN Peta



        )
        Log.e("ooooo",latitude.toString()+"   "+longitude.toString())
        var userLatitude = latitude // Latitude of user's location
        var userLongitude = longitude // Longitude of user's location
        var isWithinRadius = false

        for (location in locationA) {
            val distance = calculateDistance(
                location.first,
                location.second,
                userLatitude,
                userLongitude
            )

            if (distance <= 1.0) {
                isWithinRadius = true
                break
            }
        }

        if (isWithinRadius) {
            Toast.makeText(requireContext(), "Be care full you are in RED ZONE ", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "You are in green zone", Toast.LENGTH_SHORT).show()
        }
    }
    private fun isLocationPermissionGranted(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), locationPermissions, permissionRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (isLocationPermissionGranted()) {
                // Location permission granted, handle your logic here
            } else {
                // Location permission denied, handle the scenario
                // You may display a message or disable functionality that requires location access
            }
        }
    }
}







