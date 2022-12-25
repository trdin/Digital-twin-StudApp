package com.example.studapp


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import kotlin.math.log10
import kotlin.math.round

class MainActivity : AppCompatActivity() {


    lateinit var button_start_recording: Button;
    lateinit var button_stop_recording:Button;
    lateinit var button_pause_recording:Button;

    lateinit var mainHandler: Handler

    private var updateTextTask: Runnable?  = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }

        button_start_recording = findViewById(R.id.button_start_recording)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
           updateTextTask =   object : Runnable {
                override fun run() {
                    var noiseRecorder = NoiseRecorder();
                    noiseRecorder.context = this@MainActivity;
                    //Log.d("aaa", noiseRecorder.noiseLevel.toString())
                    mainHandler.postDelayed(this, 10000)
                    button_start_recording.text = round(noiseRecorder.noiseLevel).toString();
                }
            }
        }




        button_start_recording.setOnClickListener {
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        }


        button_stop_recording = findViewById(R.id.button_stop_recording)

        button_stop_recording.setOnClickListener{

            Toast.makeText(this, "Decibels: soundMeter.amp", Toast.LENGTH_SHORT).show()

        }

        button_pause_recording = findViewById(R.id.button_pause_recording)

        button_pause_recording.setOnClickListener {


        }

        mainHandler = Handler(Looper.getMainLooper())
    }




    override fun onPause() {
        super.onPause()
        if(updateTextTask != null){
            mainHandler.removeCallbacks(updateTextTask!!)
        }
    }

    override fun onResume() {
        super.onResume()
        if(updateTextTask != null) {
            mainHandler.post(updateTextTask!!)
        }
    }
}