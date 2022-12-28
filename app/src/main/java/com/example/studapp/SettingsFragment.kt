package com.example.studapp

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.studapp.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var frequency: Float = 10f
    private lateinit var app: MyApplication
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = (activity?.application as MyApplication)
        (activity as MainActivity).supportActionBar?.title = "Settings"

        frequency = app.sharedPref.getFloat(frequencyString, 10f)
        binding.slFrequency.value = frequency

        binding.btnSave.setOnClickListener {
            val newFrequency = binding.slFrequency.value
            if(newFrequency != frequency) {
                val editor: SharedPreferences.Editor = app.sharedPref.edit()
                try {
                    frequency = newFrequency
                    editor.putFloat(frequencyString, newFrequency)
                    editor.apply()
                    Toast.makeText(activity, "Added", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Log.i("SharedPref", ex.message.toString())
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(activity, "Already set", Toast.LENGTH_SHORT).show()
            }
        }

    }

}