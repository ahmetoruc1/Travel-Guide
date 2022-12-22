package com.ahmetoruc.deneme.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ahmetoruc.deneme.R
import com.ahmetoruc.deneme.databinding.ActivityMapsBinding
import com.ahmetoruc.deneme.model.Post

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
     private lateinit var locationmanager: LocationManager
    private lateinit var locationlistener: LocationListener
    private lateinit var permissonLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private  var trackBoolean: Boolean? =null
    private  var selectedLatitude:Double?=null
    private  var selectedLongitude:Double?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        RegisterLauncher()

        sharedPreferences=this.getSharedPreferences("com.ahmetoruc.deneme.view", MODE_PRIVATE)
        trackBoolean=false

        selectedLatitude=0.0
        selectedLongitude=0.0

        auth= Firebase.auth
        firestore= Firebase.firestore
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //val selcuk=LatLng(38.02880174367973, 32.50940882386871)
        mMap.setOnMapLongClickListener(this)
        //on click listener ı uygulayan Maps activity sınıfına impalemt ettşğimiz GoogleMap.OnMapLongClickListener
        //olduğundan uygulayıcı olarak this(Maps Activity) verdik

        val intent=intent
        val info=intent.getStringExtra("info")

        if (info=="new"){

            locationmanager=this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationlistener= object :LocationListener{
                override fun onLocationChanged(p0: Location) {
                    trackBoolean=sharedPreferences.getBoolean("trackBoolean",false)
                    if (trackBoolean==false){
                        val userLocation=LatLng(p0.latitude, p0.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                        sharedPreferences.edit().putBoolean("trackBoolean",true).apply()
                    }
                }
            }

            if (ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity,android.Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"PERMİSSON NEEDED FOR LOCATİON",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        //izin istenecek
                        permissonLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
                }else{
                    //izin istenecek
                    permissonLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }else{
                //izin verildi
                locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationlistener)
                val lastLocation=locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation!=null){
                    val lastUserLocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                }
                mMap.isMyLocationEnabled=true
            }
        }else{
            mMap.clear()
            val getmap=intent.getSerializableExtra("selectedPlace") as Post
            getmap.let {
                val latlng= it.selectedLatitude?.let { it1 -> it.selectedLongitude?.let { it2 ->
                    LatLng(it1,
                        it2
                    )
                } }
                latlng?.let { it1 -> (MarkerOptions()).position(it1).title(it.title) }
                    ?.let { it2 -> mMap.addMarker(it2) }
                latlng?.let { it1 -> CameraUpdateFactory.newLatLngZoom(it1,15f) }
                    ?.let { it2 -> mMap.moveCamera(it2) }
            }
            binding.buttonSave.visibility=View.GONE
        }
    }

    private fun RegisterLauncher(){
        permissonLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if (result){
                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationlistener)

                    val lastLocation=locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation!=null){
                        val lastUserLocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                    }
                    mMap.isMyLocationEnabled=true
                }
            }else{
                Toast.makeText(this,"Permission Needed",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))

        selectedLatitude=p0.latitude
        selectedLongitude=p0.longitude
        // seçilen konumun enlem ve boylam boylamı atandı
    }

    fun save(view:View){
        //Intent ekleyip Details activiteye dönmesi gerekiyor

        //val latlng=selectedLatitude.toString()+selectedLongitude.toString()

        val intent= Intent(this@MapsActivity,DetailsActivity::class.java)
        intent.putExtra("latitude",selectedLatitude)
        intent.putExtra("longitude",selectedLongitude)
        setResult(1,intent)
        finish()
    }

}