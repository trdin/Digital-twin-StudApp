package com.example.studapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*


class MyApplication : Application() {
    lateinit var sharedPref: SharedPreferences
    lateinit var okClient: OkHttpClient
    var frequency = 1.0f

    companion object {
        val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
        val MEDIA_TYPE_PNG = "image/png".toMediaType()

        const val SHARED_NAME: String = "sharedData.data"
        const val FREQUENCY_STRING: String = "frequency"
        const val MAIN_API_URL: String = "https://api.smltg.eu/"
//        const val MAIN_API_URL: String = "http://192.168.1.115:8080/"

        //const val BLOCKCHAIN_API_URL: String = "http://192.168.31.223:3000/"
        const val BLOCKCHAIN_API_URL: String =
            "https://httpstat.us/200/" // Dev URL use, when not using chain
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        // allow network tasks on main thread
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        initShared()

        // okHTTP
        okClient = OkHttpClient()

    }

    private fun initShared() {
        sharedPref = getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        if (!sharedPref.contains(FREQUENCY_STRING)) {
            try {
                editor.putFloat(FREQUENCY_STRING, 10f)
                editor.apply()
            } catch (ex: Exception) {
                Timber.tag("SharedPref").e(ex.message.toString())
            }
        } else {
            frequency = sharedPref.getFloat(FREQUENCY_STRING, 10f)
        }
    }

    /** okHTTP */
    private fun getRequest(url: String, resource: String): String {
        var result = ""
        val request = Request.Builder()
            .url(url + resource)
            .build()
        try {
            val response = okClient.newCall(request).execute()
            result = response.body!!.string()
        } catch (ex: Exception) {
            println("GET REQUEST ERROR " + ex.message)
        }
        return result
    }

    private fun postRequest(url: String, resource: String, postBody: String): String {
        val postBodyStr = postBody.trimMargin()
        val request = Request.Builder()
            .url(url + resource)
            .post(postBodyStr.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        okClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    fun postImageRequest(image: File, lat: String, lon: String): String {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", image.name, image.asRequestBody("image/jpeg".toMediaType()))
            .addFormDataPart("people", "0")
            .addFormDataPart("time", Date().toString())
            .addFormDataPart("lat", lat)
            .addFormDataPart("lon", lon)
            .build()


        val request = Request.Builder()
            .url(MAIN_API_URL + "images")
            .post(requestBody)
            .build()

        okClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    // Main API
    fun getMainRequest(resource: String): String {
        return getRequest(MAIN_API_URL, resource)
    }

    fun postMainRequest(resource: String, postBody: String): String {
        return postRequest(MAIN_API_URL, resource, postBody)
    }

    // Blockchain API
    fun getRequestChain(resource: String): String {
        return getRequest(BLOCKCHAIN_API_URL, resource)
    }

    fun postChain(resource: String, postBody: String): String {
        return postRequest(BLOCKCHAIN_API_URL, resource, postBody)
    }
}