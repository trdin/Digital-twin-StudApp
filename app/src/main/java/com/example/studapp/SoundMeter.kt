package com.example.studapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class SoundMeter {
    private var ar: AudioRecord? = null
    private var minSize = 0
    lateinit var context: Context;
    fun start() {
        minSize = AudioRecord.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
            ar = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minSize
            )
            ar!!.startRecording()
        }

    }

    fun stop() {
        if (ar != null) {
            ar!!.stop()
        }
    }

    val amplitude: Double
        get() {
            val buffer = ShortArray(minSize)
            ar!!.read(buffer, 0, minSize)
            var max = 0
            for (s in buffer) {
                if (Math.abs(s.toInt()) > max) {
                    max = Math.abs(s.toInt())
                }
            }
            return max.toDouble()
        }
}