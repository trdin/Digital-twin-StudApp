package com.example.studapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.studapp.databinding.FragmentDevBinding
import com.example.studapp.utils.NoiseJsonObject
import com.google.gson.Gson
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DevFragment : Fragment() {

    private var _binding: FragmentDevBinding? = null
    private val binding get() = _binding!!
    private lateinit var app: MyApplication
    lateinit var mainHandler: Handler

    private var lat: String = "lateinit"
    private var lon: String = "lateinit"
    private var noiseMin: Int = 0
    private var noiseMax: Int = 0
    private var interval: Double = 0.0
    private var repetitions: Int = 0
    private var simIsRunning: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = (activity?.application as MyApplication)
        mainHandler = Handler(Looper.getMainLooper())

        binding.btnDevSimStart.setOnClickListener {
            if (binding.etDevSimRangeMin.text.toString() == "") {
                Toast.makeText(context, "Min range empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (binding.etDevSimRangeMax.text.toString() == "") {
                Toast.makeText(context, "Max range empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (binding.etDevSimInterval.text.toString() == "") {
                Toast.makeText(context, "Interval empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (binding.etDevSimRepeat.text.toString() == "") {
                Toast.makeText(context, "Repetitions empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            noiseMin = binding.etDevSimRangeMin.text.toString().toInt()
            noiseMax = binding.etDevSimRangeMax.text.toString().toInt()
            interval = binding.etDevSimInterval.text.toString().toDouble()
            repetitions = binding.etDevSimRepeat.text.toString().toInt()
            when (binding.spDevSimulatedPlaces.selectedItem.toString()) {
                "Eat smart FERI" -> { // Eat smart FERI
                    lat = "46.5594876"; lon = "15.6392718"
                }
                "Eat smart Koroška" -> { // Eat smart Koroška
                    lat = "46.5595476"; lon = "15.6338033"
                }
                "Baščaršija Gosposvetska" -> { // Baščaršija Gosposvetska
                    lat = "46.5620837"; lon = "15.6323094"
                }
                "Mango" -> { // Mango
                    lat = "46.56316907"; lon = "15.62965699"
                }
                else -> { // Papagayo
                    lat = "46.557861"; lon = "15.646434"
                }
            }
            if (simIsRunning) {
                Toast.makeText(context, "Stopping previous simulation", Toast.LENGTH_SHORT).show()
                removeRunSim()
            }
            mainHandler.post(runSim)
        }

        binding.btnDevSimStop.setOnClickListener {
            removeRunSim()
        }

        binding.btnDevGetReq.setOnClickListener {
            try {
                val str = app.getMainRequest("restaurants") // TODO change to messages
                binding.tvDevGetReq.text = str
            } catch (ex: IOException) {
                binding.tvDevGetReq.text = ex.toString()
            }
        }


        /*binding.btnDevPostReq.setOnClickListener {
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
                binding.tvDevPostReq.text = str
            } catch (ex: IOException) {
                binding.tvDevPostReq.text = ex.toString()
            }
        }*/
    }

    override fun onPause() {
        super.onPause()
        removeRunSim()
    }


    private val runSim = object : Runnable {
        override fun run() {
            simIsRunning = true
            if (repetitions > 0) {
                val noiseDb = (noiseMin..noiseMax).random().toDouble()
                val jsonObj = NoiseJsonObject()
                jsonObj.noise = noiseDb
                jsonObj.lat = lat
                jsonObj.lon = lon
                val time = Calendar.getInstance().time
                jsonObj.time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(time).toString()

                println(Gson().toJson(jsonObj))
                try {
                    app.postChain("noise", Gson().toJson(jsonObj))
                } catch (ex: IOException) {
                    Timber.e(ex)
                }
                repetitions--
                mainHandler.postDelayed(this, (interval * 1000).toLong())
            } else simIsRunning = false
        }
    }

    private fun removeRunSim() {
        simIsRunning = false
        mainHandler.removeCallbacks(runSim)
    }

}