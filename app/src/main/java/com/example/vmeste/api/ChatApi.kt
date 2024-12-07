package com.example.vmeste.api

import com.example.vmeste.models.Chat
import retrofit2.Call
import retrofit2.http.GET

interface ChatApi {
    @GET("chats")
    fun getChats(): Call<List<Chat>>
}