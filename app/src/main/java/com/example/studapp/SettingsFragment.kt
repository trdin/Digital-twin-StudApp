package com.example.studapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.studapp.databinding.FragmentSettingsBinding


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
        //Set button and switch to users prefs.
        setItems()
        binding.swRecorderSetting.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.tvSwitchStatus.setText("Enabled")
                binding.slFrequency.isEnabled = true
            }else {
                binding.tvSwitchStatus.setText("Disabled")
                binding.slFrequency.isEnabled = false
            }
        }

        binding.btnSave.setOnClickListener {
            val newFrequency = binding.slFrequency.value
            val newRecordingSetting = binding.swRecorderSetting.isChecked
            if(app.saveFrequency(newFrequency) && app.saveRecordSetting(newRecordingSetting)){
                Toast.makeText(activity, "Settings Saved", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity, "Settings Error", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOpenDev.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.baseFragment, DevFragment())
                addToBackStack(null)
                commit()
            }
        }

    }

    fun setItems() {
        binding.slFrequency.value = app.frequency
        binding.slFrequency.isEnabled = app.recordSetting
        binding.swRecorderSetting.isChecked = app.recordSetting
        if(app.recordSetting) {
            binding.tvSwitchStatus.setText("Enabled")
        }else {
            binding.tvSwitchStatus.setText("Disabled")
        }
    }
}