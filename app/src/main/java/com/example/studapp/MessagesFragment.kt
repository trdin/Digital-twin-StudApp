package com.example.studapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studapp.databinding.FragmentMessagesBinding
import com.example.studapp.utils.SerializableMessageObject
import com.google.gson.Gson



class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var app: MyApplication
    private var messages: List<SerializableMessageObject> = listOf()
    private var selectedMessages: MutableList<SerializableMessageObject> = mutableListOf()
    private var categoryArray = arrayListOf("Select Category")
    private var selectedCategory: String = "Select Category"
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter:
            RecyclerView.Adapter<MessagesAdapter.ViewHolder>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        app = (activity?.application as MyApplication)

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMessagesFromAPI()
        fillCategories()
        // Set adapter for drop down menu
        val dropDownMenu: AutoCompleteTextView = binding.autoCompleteTextView
        arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryArray)
        dropDownMenu.setAdapter(arrayAdapter)

        filterCategories()

        //Set adapter and layout manager for recycler view
        layoutManager = LinearLayoutManager(context)
        binding.rvMessages.layoutManager = layoutManager
        adapter = MessagesAdapter(selectedMessages)
        binding.rvMessages.adapter = adapter

        dropDownMenu.setOnItemClickListener { parent, _, position, id ->
            selectedCategory = categoryArray[position]
            filterCategories()
            adapter?.notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()

    }

    fun filterCategories(){
        selectedMessages.removeAll(selectedMessages)
        for(message in messages) {
            if(message.category == selectedCategory || selectedCategory == "Select Category") {
                selectedMessages.add(message)
            }
        }
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

