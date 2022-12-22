package com.ahmetoruc.deneme.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmetoruc.deneme.databinding.CardViewBinding
import com.ahmetoruc.deneme.model.Post
import com.ahmetoruc.deneme.view.MapsActivity
import com.squareup.picasso.Picasso

class PostAdapter(val postList:ArrayList<Post>):RecyclerView.Adapter<PostAdapter.CardHolder>() {
    class CardHolder(val binding:CardViewBinding):RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val binding=CardViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CardHolder(binding)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val info=postList.get(position).city+", "+postList.get(position).country
        holder.binding.infoTextHp.text=info
        holder.binding.titleTextHp.text=postList.get(position).title
        holder.binding.usernameTextHp.text=postList.get(position).email

        holder.itemView.setOnClickListener{
            val intent= Intent(holder.itemView.context,MapsActivity::class.java)
            intent.putExtra("selectedPlace",postList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.CardViewImageView)

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}

