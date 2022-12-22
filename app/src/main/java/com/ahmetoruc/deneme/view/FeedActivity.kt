package com.ahmetoruc.deneme.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout.HORIZONTAL
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.ahmetoruc.deneme.R
import com.ahmetoruc.deneme.adapter.PostAdapter
import com.ahmetoruc.deneme.databinding.ActivityFeedBinding
import com.ahmetoruc.deneme.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var postArraylist:ArrayList<Post>
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        auth= Firebase.auth
        db=Firebase.firestore
        getData()
        postArraylist=ArrayList<Post>()

        binding.recyclerViewHomePage.layoutManager=LinearLayoutManager(this)
        postAdapter= PostAdapter(postArraylist)
        binding.recyclerViewHomePage.adapter=postAdapter
        binding.Actionbutton.setOnClickListener{
            val intent=Intent(this@FeedActivity,MapsActivity2::class.java)
            startActivity(intent)

        }

    }

     private fun getData(){
        db.collection("Post").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                //orderBy("date",Query.Direction.DESCENDING) son paylaşılanın en üstte olacak şekilde akışta olmasını sağlar
            if (error!=null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (value!=null){
                    if (!value.isEmpty){
                        val documents=value.documents
                        postArraylist.clear()
                        //array listteki elamanları tamizliyoruz ki akışta aynı posttan tekrar gözükmesin diye
                        for (doc in documents){
                            val email=doc.get("userEmail").toString()
                            val city=doc.get("city").toString()
                            val country=doc.get("country").toString()
                            val title=doc.get("title").toString()
                            val url=doc.get("downloadUrl").toString()
                            val latitude=doc.get("latitude")?.let {
                                it as Double
                            }

                            val logitude=doc.get("longitude")?.let {
                                it as Double
                            }

                            val post=Post(email,city,country,title,url,latitude,logitude)
                            postArraylist.add(post)

                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
    fun getmap(view: View){
        val intent=Intent(this@FeedActivity,MapsActivity::class.java)
        startActivity(intent)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.add_post){
            val intent=Intent(this, DetailsActivity::class.java)
            startActivity(intent)
        }else if(item.itemId== R.id.signout){
            auth.signOut()
            val intent=Intent(this, activity_loginscreen3::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }






}