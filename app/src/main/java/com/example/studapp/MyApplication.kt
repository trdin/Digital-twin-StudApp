package com.example.studapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.IOException


class MyApplication : Application() {
    lateinit var sharedPref: SharedPreferences
    lateinit var okClient: OkHttpClient

    companion object {
        val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

        const val SHARED_NAME: String = "sharedData.data"
        const val FREQUENCY_STRING: String = "frequency"
        const val MAIN_API_URL: String = "https://api.smltg.eu/"
        const val BLOCKCHAIN_API_URL: String = "http://192.168.0.21:3000/"
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
        }
    }

    // okHTTP
    fun getRequest(resource: String): String {
        val request = Request.Builder()
            .url(MAIN_API_URL + resource)
            .build()

        okClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    fun postRequest(resource: String, postBody: String): String {
        val postBodyStr = postBody.trimMargin()
        val request = Request.Builder()
            .url(MAIN_API_URL + resource)
//            .url("https://reqres.in/api/users") // TODO remove testing url
            .post(postBodyStr.toRequestBody(MEDIA_TYPE_MARKDOWN))
            .build()

        okClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }


    fun getRequestChain(resource: String): String {
        val request = Request.Builder()
            .url(BLOCKCHAIN_API_URL + resource)
            .build()

        okClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    fun postChain(resource: String, postBody: String): String {
       // val postBodyStr = postBody.trimMargin()

        val request = Request.Builder()
            .url(BLOCKCHAIN_API_URL + resource)
//            .url("https://reqres.in/api/users") // TODO remove testing url
            .post(postBody.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        okClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }


}