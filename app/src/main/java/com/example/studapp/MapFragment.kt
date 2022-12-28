package com.example.studapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studapp.databinding.FragmentHomeBinding
import com.example.studapp.databinding.FragmentMapBinding
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    var startPoint: GeoPoint = GeoPoint(46.553421, 15.645539);

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        org.osmdroid.config.Configuration.getInstance()
            .load(activity?.applicationContext, activity?.getPreferences(Context.MODE_PRIVATE))

        _binding = FragmentMapBinding.inflate(inflater, container, false)

        map = binding.mvMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true)
        map.controller.setCenter(startPoint)
        map.controller.setZoom(15)
        mapController = map.controller


        return binding.root
    }


}