package com.example.studapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studapp.databinding.ItemMessageBinding
import com.example.studapp.utils.SerializableMessageObject

class MessagesAdapter(private val messages: List<SerializableMessageObject>): RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root) {
        val itemCategory: TextView = binding.tvCategory
        val itemContent: TextView = binding.tvContent
        val itemTime: TextView = binding.tvTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val message: SerializableMessageObject = messages[position]
        val time = "${message.time.hours}:${message.time.minutes}"
        holder.itemCategory.text = message.category
        holder.itemContent.text = message.content
        holder.itemTime.text = time
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
