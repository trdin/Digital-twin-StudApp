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


class SettingsFragment : Fragment()  {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
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


        binding.slFrequency.value = app.frequency
        binding.recorderSetting.isChecked = app.recordSetting

        binding.btnSave.setOnClickListener {
            val newFrequency = binding.slFrequency.value
            val newRecordingSetting = binding.recorderSetting.isChecked
            if(app.saveFrequency(newFrequency) && app.saveRecordSetting(newRecordingSetting)){
                Toast.makeText(activity, "Added", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
            }
            /*if (newFrequency != app.frequency) {
                val editor: SharedPreferences.Editor = app.sharedPref.edit()
                try {
                    app.frequency = newFrequency
                    editor.putFloat(MyApplication.FREQUENCY_STRING, newFrequency)
                    editor.apply()
                    Toast.makeText(activity, "Added", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Timber.tag("SharedPref").e(ex.message.toString())
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Already set", Toast.LENGTH_SHORT).show()
            }*/
        }

        binding.btnOpenDev.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.baseFragment, DevFragment())
                addToBackStack(null)
                commit()
            }
        }

    }
}