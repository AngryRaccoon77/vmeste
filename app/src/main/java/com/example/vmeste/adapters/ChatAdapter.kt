package com.example.vmeste.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vmeste.databinding.ItemChatBinding
import com.example.vmeste.models.Chat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.vmeste.R

class ChatAdapter(private var chatList: List<Chat>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    fun updateData(newChatList: List<Chat>) {
        chatList = newChatList
        notifyDataSetChanged()
    }
    class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.senderName.text = chat.senderName
            binding.lastMessage.text = chat.lastMessage
            binding.time.text = chat.time
            Glide.with(binding.profileImage.context)
                .load(chat.profileImage)
                .placeholder(R.drawable.logo)
                .transform(CircleCrop())
                .into(binding.profileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size
}

