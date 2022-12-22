package com.ahmetoruc.deneme.view

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmetoruc.deneme.R
import com.ahmetoruc.deneme.adapter.RVHorizontalAdapter
import com.ahmetoruc.deneme.databinding.ActivityMaps2Binding
import com.ahmetoruc.deneme.databinding.CardViewHorizontalBinding
import com.ahmetoruc.deneme.model.ButtonName
import com.ahmetoruc.deneme.model.MapCategories

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MapsActivity2 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMaps2Binding
    private lateinit var db: FirebaseFirestore
    private lateinit var mapArraylist: ArrayList<MapCategories>
    private lateinit var locationmanager: LocationManager
    private lateinit var locationlistener: LocationListener
    private lateinit var permissonLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private  var trackBoolean: Boolean? =null
    private lateinit var buttonList:ArrayList<ButtonName>
    private lateinit var binding2:CardViewHorizontalBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //mapArraylist = ArrayList<MapCategories>()
        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        db = Firebase.firestore
        getData()
        mapArraylist = ArrayList<MapCategories>()
        RegisterLauncher()
        sharedPreferences=this.getSharedPreferences("com.ahmetoruc.deneme.view", MODE_PRIVATE)
        trackBoolean=false

        buttonList=ArrayList<ButtonName>()
        //binding.RecyclerViewHorizontal.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        //val rvHorizontalAdapter=RVHorizontalAdapter(buttonList)

        //binding.RecyclerViewHorizontal.adapter=rvHorizontalAdapter

        //binding2=CardViewHorizontalBinding.inflate(layoutInflater)
        //setContentView(binding2.root)


        /*val restoranlar=ButtonName("Restoranlar")
        val oteller=ButtonName("Oteller")
        val kafeler=ButtonName("Kafeler")
        val eczaneler=ButtonName("Eczaneler")
        val tarihiMekanlar=ButtonName("Tarihi Mekanlar")


        buttonList.add(restoranlar)
        buttonList.add(oteller)
        buttonList.add(kafeler)
        buttonList.add(eczaneler)
        buttonList.add(tarihiMekanlar)*/

    binding.cardViewRestoran.setOnClickListener{
        mapArraylist.forEach {
            val maplocation = LatLng(it.latitude!!, it.longitude!!)
            if (it.kategori=="Restoran"){
                //farklı textlere göre yapılacak
                //val res=R.drawable.restoranlar

                mMap.addMarker(
                    MarkerOptions().position(maplocation)
                        .icon(bitmapDescriptorFromVector(this, R.drawable.restoranlar))
                )
            }
        }
    }



    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //val userLocation=LatLng(38.02263594375468, 32.51359920948744)
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))


        locationmanager=this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationlistener= object :LocationListener{
            override fun onLocationChanged(p0: Location) {
                trackBoolean=sharedPreferences.getBoolean("trackBoolean",false)
                if (trackBoolean==false)
                {
                     val userLocation=LatLng(p0.latitude,p0.longitude)
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                     sharedPreferences.edit().putBoolean("trackBoolean",true).apply()

                    //mapArraylist.forEach {
                        //val maplocation=LatLng(it.latitude!!, it.longitude!!)
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(maplocation))
                    //}
                }

                //val userLocation=LatLng(p0.latitude, p0.longitude)
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
            }


        }





        if(ContextCompat.checkSelfPermission(this@MapsActivity2,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity2,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"PERMİSSON NEEDED FOR LOCATİON",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    permissonLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                permissonLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else
        {
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationlistener)
            val lastLocation=locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation!=null){
                val lastUserLocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
            }
            mMap.isMyLocationEnabled=true
        }

    }

    public fun filterClickRestoran(view:View) {

         mapArraylist.forEach {
             val maplocation = LatLng(it.latitude!!, it.longitude!!)
             if (it.kategori=="Restoran"){
                 //farklı textlere göre yapılacak
                 //val res=R.drawable.restoranlar

                 mMap.addMarker(
                     MarkerOptions().position(maplocation)
                         .icon(bitmapDescriptorFromVector(this, R.drawable.restoranlar))
                 )
             }
         }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun getData() {
        db.collection("Post").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        for (doc in documents) {
                            val latitude = doc.get("latitude")?.let {
                                it as Double
                            } ?: 0.0

                            val logitude = doc.get("longitude")?.let {
                                it as Double
                            } ?: 0.0
                            val kategori = doc.get("kategori").toString()

                            val map = MapCategories(latitude, logitude, kategori)
                            mapArraylist.add(map)
                        }
                    }
                }
            }

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
}


