package com.example.studapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.studapp.databinding.FragmentMessagesBinding
import com.example.studapp.utils.SerializableMessageObject
import com.google.gson.Gson


class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var app: MyApplication
    private var messages: List<SerializableMessageObject> = listOf()
    private val categoryArray = arrayListOf("Select Category")
    private lateinit var arrayAdapter: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = (activity?.application as MyApplication)

        getMessagesFromAPI()
        fillCategories()
        val dropDownCategories: AutoCompleteTextView = binding.autoCompleteTextView
        arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryArray)
        dropDownCategories.setAdapter(arrayAdapter)

    }

    override fun onResume() {
        super.onResume()

    }

    fun fillCategories(){
        if(!messages.isEmpty()) {
            for(message in messages) {
                val category = message.category
                if(!categoryArray.contains(category)){
                    categoryArray.add(category)
                }
            }
        }
    }

    fun getMessagesFromAPI() {
        val response = app.getMainRequest("messages")
        messages = Gson().fromJson(response, Array<SerializableMessageObject>::class.java).toList()
    }
}

