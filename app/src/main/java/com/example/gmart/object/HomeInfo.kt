package com.example.gmart.`object`

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

object HomeInfo {

    // Buat antrian permintaan Volley
    lateinit var requestQueue: RequestQueue
    var infoString = "Loading. . ."

    private val _liveInfoString = MutableLiveData<String>().apply {
    value = infoString
    }
    val liveInfoString: LiveData<String> = _liveInfoString

    fun getHomeInfoData (context: Context){
        requestQueue = Volley.newRequestQueue(context)
        val url = "https://wifi-map.my.id/api/gmart_get_info.php"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Ambil data pertama dari JSON Array
                    val data = response.getJSONObject(0)
                    val textData = data.getString("inf_string") // Ganti dengan nama kolom yang sesuai
                    // Masukan data ke LiveView
                    _liveInfoString.postValue(textData.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                // Tangani error
            })
        requestQueue.add(jsonArrayRequest)
    }

    fun getHomeInfoData2 (){
        requestQueue = RQ.getRQ()
        val url = "https://wifi-map.my.id/api/gmart_get_info.php"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Ambil data pertama dari JSON Array
                    val data = response.getJSONObject(0)
                    val textData = data.getString("inf_string") // Ganti dengan nama kolom yang sesuai
                    // Masukan data ke LiveView
                    _liveInfoString.postValue(textData.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                // Tangani error
            })
        requestQueue.add(jsonArrayRequest)
    }
}