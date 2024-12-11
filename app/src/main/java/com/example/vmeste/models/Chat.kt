package com.example.vmeste.models


data class Chat(
    val id: Int,
    val senderName: String,
    val lastMessage: String,
    val profileImage: String,
    val time: String
)
