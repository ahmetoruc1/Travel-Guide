package com.ahmetoruc.deneme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmetoruc.deneme.databinding.CardViewHorizontalBinding

import com.ahmetoruc.deneme.model.ButtonName

class RVHorizontalAdapter(val buttonList: ArrayList<ButtonName>):RecyclerView.Adapter<RVHorizontalAdapter.CardHolder>() {
    class CardHolder(val binding: CardViewHorizontalBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val binding=CardViewHorizontalBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CardHolder(binding)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.binding.textView.text=buttonList.get(position).name

    }

    override fun getItemCount(): Int {
        return buttonList.size
    }
}