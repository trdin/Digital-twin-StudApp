package com.example.studapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studapp.databinding.FragmentHomeBinding
import com.example.studapp.utils.ImageJsonObject
import com.example.studapp.utils.MessageJsonObject
import com.google.gson.Gson
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var app: MyApplication
    private lateinit var sendFile: File
    private var sendFileToApi: Boolean = false

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
        binding.btnSendImg.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {

            val uri = data.data!!

            sendFile = getFileFromURI(uri)
            val lastLocation = (activity as MainActivity).getLastKnownLocation()
            if (lastLocation != null)
                app.postImageRequest(
                    sendFile,
                    lastLocation.latitude.toString(),
                    lastLocation.longitude.toString()
                )
        }
    }

    private fun getFileFromURI(contentUri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(contentUri)
        val buffer = ByteArray(8192)
        var bytesRead: Int
        val output = ByteArrayOutputStream()
        while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        val file = File(requireContext().getExternalFilesDir(null), "temp.jpg")
        file.writeBytes(output.toByteArray())
        return file
    }
}