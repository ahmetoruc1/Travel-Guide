package com.ahmetoruc.deneme.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ahmetoruc.deneme.R
import com.ahmetoruc.deneme.databinding.ActivityDetailsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailsBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<String>
    var selectedPicture:Uri?=null
   private lateinit var auth:FirebaseAuth
   private lateinit var firestore:FirebaseFirestore
   private lateinit var storage:FirebaseStorage
   private var latitude=0.0
   private var longitude=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailsBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        registerLauncher()


        auth=Firebase.auth
        firestore=Firebase.firestore
        storage=Firebase.storage

        val kategoriler
                = resources.getStringArray(R.array.Kategoriler)

        val adapter
                = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, kategoriler)
        binding.autoCompleteText.setAdapter(adapter)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==1){
            latitude= data?.getDoubleExtra("latitude",0.0) ?: 0.0
            longitude=data?.getDoubleExtra("longitude",0.0) ?: 0.0
        }

    }
    fun save(view: View){
        //universal uniq id
        val uuid=UUID.randomUUID()
        val imageName="$uuid.jpg"

        val referans=storage.reference
        val imagereferans=referans.child("images").child(imageName)

        if (selectedPicture!=null){
            imagereferans.putFile(selectedPicture!!).addOnSuccessListener {
                //indirilen görseli ->firestora a kaydet

                val intent=getIntent()


                val uploadPictureReferance=storage.reference.child("images").child(imageName)
                uploadPictureReferance.downloadUrl.addOnSuccessListener {
                    val downloadUrl=it.toString()
                    if (auth.currentUser!=null){
                        val postMap= hashMapOf<String ,Any>()
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!)
                        postMap.put("title",binding.titleText.text.toString())
                        postMap.put("city",binding.cityText.text.toString())
                        postMap.put("country",binding.countryText.text.toString())
                        postMap.put("date",com.google.firebase.Timestamp.now())
                        //postMap.put("latlng",latlng!!)
                        postMap.put("latitude",latitude)
                        postMap.put("longitude",longitude)
                        postMap.put("kategori",binding.autoCompleteText.text.toString())

                        firestore.collection("Post").add(postMap).addOnSuccessListener {

                            finish()
                        }.addOnFailureListener {ex->
                            Toast.makeText(this,ex.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }


    fun selectLocation(view: View){
        val intent=Intent(this@DetailsActivity,MapsActivity::class.java)
        intent.putExtra("info","new")
        startActivityForResult(intent,1)
    }


    fun ImageSave(view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for Gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else{
                permissionResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        else{
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }


    fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            binding.imageView4.setImageURI(it)
                        }
                    }
                }
            }

        permissionResultLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                val intenToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intenToGallery)
            } else {
                Toast.makeText(this@DetailsActivity, "Permisson Needed", Toast.LENGTH_LONG).show()
            }
        }
    }
}