package com.example.vmeste.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vmeste.adapters.ChatAdapter
import com.example.vmeste.databinding.FragmentHomeBinding
import com.example.vmeste.models.Chat
import com.example.vmeste.R
import com.example.vmeste.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private lateinit var chatAdapter: ChatAdapter
    private var chatList: List<Chat> = emptyList()

    companion object {
        const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatAdapter(chatList)
        binding.recyclerView.adapter = chatAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchChats()
        }

        fetchChats()
    }

    private fun fetchChats() {
        RetrofitClient.instance.getChats().enqueue(object : Callback<List<Chat>> {
            override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
                if (response.isSuccessful && response.body() != null) {
                    chatList = response.body()!!
                    chatAdapter.updateData(chatList)
                    Log.d(TAG, "Chats fetched successfully")
                } else {
                    Log.e(TAG, "Response is not successful")
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
                Log.e(TAG, "API call failed", t)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView called")
    }
}