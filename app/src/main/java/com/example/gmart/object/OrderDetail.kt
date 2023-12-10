package com.example.gmart.`object`

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.gmart.dataClass.OrderDetailProducts
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object OrderDetail {

    var orderDetail = JSONArray()
    private val _liveOrderDetail = MutableLiveData<JSONArray>().apply {
        orderDetail
    }
    val liveOrderDetail: LiveData<JSONArray> = _liveOrderDetail

    fun getOrderDetailFromDB (orderId : String) {
        val url = "https://wifi-map.my.id/api/gmart_get_order_detail.php?order_id=$orderId"
        val jsonArrayReq = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    clearData()
                    // Ambil data pertama dari JSON Array
                    _liveOrderDetail.postValue(response)
                    orderDetail = response
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("err",error.toString())
            })
        RQ.getRQ().add(jsonArrayReq)
    }

    fun getOrderProductArrayList () : ArrayList<OrderDetailProducts> {
        val list = ArrayList<OrderDetailProducts>()

        for (i in 0 until orderDetail.length()){
            var jsonObject : JSONObject = orderDetail.getJSONObject(i)
            val id = jsonObject.getString("prd_id")
            val nama = jsonObject.getString("prd_nama")
            val harga = jsonObject.getString("prd_harga").toInt()
            val jumlah = jsonObject.getString("ord_jumlah").toInt()
            val url = jsonObject.getString("prd_foto")
            val data = OrderDetailProducts(id, nama, harga,jumlah, url)
            list.add(data)
        }
        return list
    }

    fun clearData (){
        orderDetail = JSONArray()
        _liveOrderDetail.postValue(orderDetail)
    }
}