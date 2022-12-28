package com.example.studapp


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.studapp.databinding.ActivityMainBinding
import com.example.studapp.location.MyEventLocationSettingsChange
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*
import kotlin.math.log10
import kotlin.math.round

class MainActivity : AppCompatActivity() {


    lateinit var mainHandler: Handler
    lateinit var binding: ActivityMainBinding

    private var updateTextTask: Runnable? = null;

    //location
    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient //https://developer.android.com/training/location/retrieve-current
    private var lastLoction: Location? = null
    private var locationCallback: LocationCallback
    private var locationRequest: LocationRequest
    private var requestingLocationUpdates = false

    init {

        //fixed the decprication
        locationRequest = LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(3000)
            .setMaxUpdateDelayMillis(100)
            .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    //updateLocation(location) //MY function
                    this@MainActivity.lastLoction = location;
                }
            }
        }
        this.activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            Log.d("aaa", "$allAreGranted")
            if (allAreGranted) {
                initCheckLocationSettings()
                //initMap() if settings are ok
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }*/
        val homeFragment: Fragment = HomeFragment()
        val mapFragment: Fragment = MapFragment()
        val settingsFragment: Fragment = SettingsFragment()

        replaceFragment(homeFragment)

        binding.btvNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.icHome -> {
                    replaceFragment(homeFragment)
                    true
                }
                R.id.icMap -> {
                    replaceFragment(mapFragment)
                    true
                }
                R.id.icSettings -> {
                    replaceFragment(settingsFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }

        val appPerms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
        )
        activityResultLauncher.launch(appPerms)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            updateTextTask = object : Runnable {
                override fun run() {
                    var noiseRecorder = NoiseRecorder();
                    noiseRecorder.context = this@MainActivity;
                    //Log.d("aaa", noiseRecorder.noiseLevel.toString())
                    mainHandler.postDelayed(this, 10000)
                    var noiseDb = round(noiseRecorder.noiseLevel);

                    var time = Date()
                    Log.d("aaa", time.toString());
                    //TODO send http request to server
                    //noise, lat, lon , time.

                    Log.d("aaa", lastLoction.toString())
                }
            }
        }

        mainHandler = Handler(Looper.getMainLooper())
    }

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.baseFragment, fragment)
            commit()
        }
    }


    override fun onPause() {
        super.onPause()
        if (updateTextTask != null) {
            mainHandler.removeCallbacks(updateTextTask!!)
        }
        if (requestingLocationUpdates) {
            requestingLocationUpdates = false
            stopLocationUpdates()
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateTextTask != null) {
            mainHandler.post(updateTextTask!!)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMsg(status: MyEventLocationSettingsChange) {
        if (status.on) {
            initLoaction()
            if (!requestingLocationUpdates) {
                requestingLocationUpdates = true
                startLocationUpdates()
            }
        } else {
            Log.d("aaa", "Stop something")
        }
    }

    fun initCheckLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            // Timber.d("Settings Location IS OK")
            MyEventLocationSettingsChange.globalState = true //default
            //initMap()
            initLoaction()
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                //Timber.d("Settings Location addOnFailureListener call settings")
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.d("aaa", "Settings Location sendEx??")
                }
            }
        }

    }

    private fun stopLocationUpdates() { //onPause
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun initLoaction() { //call in create
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        readLastKnownLocation()
    }

    @SuppressLint("MissingPermission") //permission are checked before
    fun readLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let { this.lastLoction = it }
            }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() { //onResume
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    companion object {
        val REQUEST_CHECK_SETTINGS = 20202
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                initLoaction();
            }
        }
    }
}