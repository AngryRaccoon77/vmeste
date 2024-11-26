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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: List<Chat>

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

        // Initialize dummy data
        chatList = listOf(
            Chat("Разумовский", "Сыграем в игру?", R.mipmap.razum, "10:30 AM"),
            Chat("Юля❤\uFE0F", "Как дела, Угорёк?", R.mipmap.pchelkin, "11:15 AM"),
            Chat("Дубин", "Я нашел один холодильник", R.mipmap.dubin, "1:45 PM"),
            Chat("Федор Иванович", "ИГОРЬ!!! ЗАЙДИ СРОЧНО!!!", R.mipmap.prokop, "3:20 PM")
        )

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatAdapter(chatList)
        binding.recyclerView.adapter = chatAdapter
        Log.d(TAG, "onViewCreated called")
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