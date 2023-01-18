package com.example.studapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.studapp.databinding.FragmentHomeBinding
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
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var selectedCategory: String = ""

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

        val dropDownMenu: AutoCompleteTextView = binding.tvCategories
        val categoryArray = resources.getStringArray(R.array.categories)
        arrayAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryArray
        )
        dropDownMenu.setAdapter(arrayAdapter)

        dropDownMenu.setOnItemClickListener { parent, _, position, id ->
            selectedCategory = categoryArray[position]
        }

        binding.btnSend.setOnClickListener {
            var insert = true
            val lastLocation = (activity as MainActivity).getLastKnownLocation()
            if (selectedCategory.isEmpty()) {
                Toast.makeText(context, "Select category.", Toast.LENGTH_SHORT).show()
                insert = false
            }
            if (binding.etMessage.text?.isEmpty() == false) {
                if (binding.etMessage.text?.length!! > 40) {
                    Toast.makeText(context, "Message too long!", Toast.LENGTH_SHORT).show()
                    insert = false
                }
            } else {
                insert = false
                Toast.makeText(context, "Fill message!", Toast.LENGTH_SHORT).show()
            }
            try {
                if (lastLocation != null && insert) {
                    val jsonObj = MessageJsonObject()
                    jsonObj.content = binding.etMessage.text.toString()
                    jsonObj.category = selectedCategory
                    jsonObj.latitude = lastLocation.latitude.toString()
                    jsonObj.longitude = lastLocation.longitude.toString()
                    jsonObj.time = Date().toString()
                    app.postMainRequest("messages", Gson().toJson(jsonObj))
                    Toast.makeText(context, "Message added!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.baseFragment, MessagesFragment())
                        .commit()
                }
            } catch (ex: Exception) {
                Timber.tag("Post message").e(ex)
                Toast.makeText(context, "Insertion failure!", Toast.LENGTH_SHORT).show()
                binding.etMessage.text?.clear()
                binding.tvCategories.setText("Select Category")
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
            if (lastLocation != null) {
                try {
                    app.postImageRequest(
                        sendFile,
                        lastLocation.latitude.toString(),
                        lastLocation.longitude.toString()
                    )
                    Toast.makeText(context, "Image successfully posted", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Toast.makeText(context, "Error with Image", Toast.LENGTH_SHORT).show()
                }
            }
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