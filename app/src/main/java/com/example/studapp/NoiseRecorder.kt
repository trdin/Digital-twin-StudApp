package com.example.studapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class NoiseRecorder {
    private val TAG: String = "aaa"
    lateinit var context: Context;
    //the value 51805.5336 can be derived from asuming that x=32767=0.6325 Pa and x=1 = 0.00002 Pa (the reference value)

    //making the buffer bigger....
    //recording data;
    //x=max;
    val noiseLevel: Double
        // calculating the pascal pressure based on the idea that the max amplitude (between 0 and 32767) is
        // relative to the pressure
        get() {
            Log.e(TAG, "start new recording process")
            var bufferSize = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT
            )
            //making the buffer bigger....
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bufferSize = bufferSize * 4
                val recorder = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                val data = ShortArray(bufferSize)
                var average = 0.0

                /*runBlocking { // this: CoroutineScope
                    launch { // launch a new coroutine and continue*/
                recorder.startRecording()
                //delay(2000L) // non-blocking delay for 1 second (default time unit is ms)
                //println("World!") // print after delay
                recorder.read(data, 0, bufferSize)
                recorder.stop()
                Log.e(TAG, "stop")
                for (s in data) {
                    if (s > 0) {
                        average += Math.abs(s.toInt()).toDouble()
                    } else {
                        bufferSize--
                    }
                }
                //x=max;
                val x = average / bufferSize
                Log.e(TAG, "" + x)
                recorder.release()
                Log.d(TAG, "getNoiseLevel() ")
                var db = 0.0

                // calculating the pascal pressure based on the idea that the max amplitude (between 0 and 32767) is
                // relative to the pressure
                val pressure =
                    x / 51805.5336 //the value 51805.5336 can be derived from asuming that x=32767=0.6325 Pa and x=1 = 0.00002 Pa (the reference value)
                Log.d(TAG, "x=$pressure Pa")
                db = 20 * Math.log10(pressure / 0.00002)
                Log.d(TAG, "db=$db")

                /* }
             }*/
                return db;
                //recording data;

            }
            return 0.0

        }

}