package com.example.studapp

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.studapp.databinding.FragmentMapBinding
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var app: MyApplication
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: MapView
    private lateinit var mapController: IMapController

    private var updateTextTask: Runnable? = null
    private var userLocation: Location? = null
    lateinit var mainHandler: Handler

    private var startPoint: GeoPoint = GeoPoint(46.553421, 15.645539) // Maribor Center

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        org.osmdroid.config.Configuration.getInstance()
            .load(activity?.applicationContext, activity?.getPreferences(Context.MODE_PRIVATE))

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        app = (activity?.application as MyApplication)
        map = binding.mvMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true)
        map.controller.setCenter(startPoint)
        map.controller.setZoom(16.0)
        mapController = map.controller



        updateTextTask = object : Runnable {
            override fun run() {
                mainHandler.postDelayed(this, 20000)
                userLocation = (activity as MainActivity).getLastKnownLocation()
                val latitude = userLocation?.latitude
                val longitude = userLocation?.longitude
                val point = GeoPoint(latitude!!, longitude!!)
                val marker = Marker(map)
                marker.title = "Your location"
                marker.position = point
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.icon = ContextCompat.getDrawable(
                    activity as MainActivity,
                    R.drawable.ic_my_location
                )
                map.overlays.add(marker)
                drawMarkersFromData()

            }
        }
        mainHandler = Handler(Looper.getMainLooper())


        return binding.root
    }

    private fun drawMarkersFromData() {

        val noiseData = app.getRequestChain("noise")
        if (noiseData.isNotEmpty()) {
            val jArray = try {
                JSONArray(noiseData)
            } catch (ex: Exception) {
                JSONArray("[]")
            }
            for (i in 0 until jArray.length()) {
                var lat = 0.0
                var lon = 0.0
                var time = ""
                var noise = 0.0
                val jObject: JSONObject = jArray[i] as JSONObject
                if (jObject.has("lat")) {
                    lat = jObject.getString("lat").toDouble()
                }
                if (jObject.has("lon")) {
                    lon = jObject.getString("lon").toDouble()
                }
                if (jObject.has("time")) {
                    time = jObject.getString("time")
                }
                if (jObject.has("noise")) {
                    noise = jObject.getString("noise").toDouble()
                }
                val point = GeoPoint(lat, lon)
                val marker = Marker(map)
                when (noise) {
                    in 0.0..35.0 -> {
                        marker.title = "Exceptional"
                    }
                    in 35.0..45.00 -> {
                        marker.title = "Excellent"
                    }
                    in 45.0..50.0 -> {
                        marker.title = "Good enough"
                    }
                    in 50.0..60.0 -> {
                        marker.title = "Noisy"
                    }
                    else -> {
                        marker.title = "Loud"
                    }
                }
                marker.subDescription = "Noise(Db): $noise\nRecorded: $time"
                marker.position = point
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.icon = ContextCompat.getDrawable(
                    activity as MainActivity,
                    R.drawable.ic_noise
                )
                map.overlays.add(marker)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (updateTextTask != null) {
            mainHandler.removeCallbacks(updateTextTask!!)
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateTextTask != null) {
            mainHandler.post(updateTextTask!!)
        }
    }

}