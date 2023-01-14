package com.example.studapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studapp.databinding.FragmentHomeBinding
import com.example.studapp.utils.MessageJsonObject
import com.google.gson.Gson
import timber.log.Timber
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var app: MyApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = (activity?.application as MyApplication)
        binding.btnSend.setOnClickListener {
            val lastLocation = (activity as MainActivity).getLastKnownLocation()
            try {
                if (lastLocation != null) {
                    val jsonObj = MessageJsonObject()
                    jsonObj.content = binding.etMessage.text.toString()
                    jsonObj.category = "text"
                    jsonObj.latitude = lastLocation.latitude.toString()
                    jsonObj.longitude = lastLocation.longitude.toString()
                    jsonObj.time = Date().toString()
                    app.postMainRequest("messages", Gson().toJson(jsonObj))
                }
            } catch (ex: Exception) {
                Timber.tag("Post message").e(ex)
            }
        }

    }
}