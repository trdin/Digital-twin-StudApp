package com.example.studapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.studapp.databinding.FragmentSettingsBinding
import timber.log.Timber
import java.io.IOException


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

        frequency = app.sharedPref.getFloat(MyApplication.FREQUENCY_STRING, 10f)
        binding.slFrequency.value = frequency

        binding.btnSave.setOnClickListener {
            val newFrequency = binding.slFrequency.value
            if (newFrequency != frequency) {
                val editor: SharedPreferences.Editor = app.sharedPref.edit()
                try {
                    frequency = newFrequency
                    editor.putFloat(MyApplication.FREQUENCY_STRING, newFrequency)
                    editor.apply()
                    Toast.makeText(activity, "Added", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Timber.tag("SharedPref").e(ex.message.toString())
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Already set", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDevGetReq.setOnClickListener { // TODO move to dev fragment/activity
            try {
                val str = app.getMainRequest("restaurants") // TODO change to messages
                Timber.tag("dev_get_req").d(str)
            } catch (ex: IOException) {
                Timber.tag("dev_get_req").e(ex)
            }
        }

        binding.btnDevPostReq.setOnClickListener { // TODO move to dev fragment/activity
            try {
                val strRes = """
                    |{"name": "Tester",
                        "address": "Gosposvetska cesta 83, Maribor",
                        "location": {
                            "type": "Point",
                            "coordinates": [
                                1.2345,
                                1.2345
                            ]
                        },
                        "dataSeries": "627531cfbc8191993bdd9397",
                        "_id": "62adb097ceb7bcedfab61bf7",
                        "__v": 0
                    }"""
                val str = app.postMainRequest("restaurants", strRes) // TODO change to messages
                Timber.tag("dev_post_req").d(str)
            } catch (ex: IOException) {
                Timber.tag("dev_post_req").e(ex)
            }
        }
    }
}