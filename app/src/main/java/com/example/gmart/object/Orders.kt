package com.example.gmart.`object`

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.gmart.dataClass.Order
import org.json.JSONArray
import org.json.JSONException

object Orders {
    var orders = JSONArray()
    private val _liveOrders = MutableLiveData<JSONArray>().apply {
        orders
    }
    val liveOrders: LiveData<JSONArray> = _liveOrders


    fun getFromDB (userId : String){
        val url = "https://wifi-map.my.id/api/gmart_get_orders.php?user_id=$userId"
        val jsonArrayReq = JsonArrayRequest(
                Request.Method.GET, url, null,
        { response ->
            try {
                // Ambil data pertama dari JSON Array
                _liveOrders.postValue(response)
                orders = response
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
        { error ->
            Log.d("err",error.toString())
        })
        RQ.getRQ().add(jsonArrayReq)
    }


    fun getArrayList() : ArrayList<Order> {
        val list = ArrayList<Order>()
        try {
            for (i in 0 until orders.length()) {
                val jsonObject = orders.getJSONObject(i)
                val dataField1 = jsonObject.getString("ord_id")
                val dataField2 = jsonObject.getString("ord_waktu")
                val dataField3 = jsonObject.getString("ord_status")
                val data = Order(dataField1, dataField2, dataField3, )
                list.add(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}