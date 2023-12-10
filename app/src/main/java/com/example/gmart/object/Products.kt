package com.example.gmart.`object`

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.example.gmart.dataClass.Product
import org.json.JSONArray
import org.json.JSONException

object Products {

    val requestQueue: RequestQueue = RQ.getRQ()
    val url = "https://wifi-map.my.id/api/gmart_get_all_products.php"

    var _products = JSONArray()

    private val _liveProduct = MutableLiveData<JSONArray>().apply {
        _products
    }
    val liveProduct: LiveData<JSONArray> = _liveProduct

    val jsonArrayRequest = JsonArrayRequest(
        Request.Method.GET, url, null,
        { response ->
            try {
                // Ambil data pertama dari JSON Array
                _products = response
                _liveProduct.postValue(response)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
        { error ->
            Log.d("err",error.toString())
        })

    fun getFromDB(){

        requestQueue.add(jsonArrayRequest)

    }

    fun getArrayList()  : ArrayList<Product> {
        val productArray = ArrayList<Product>()
        Log.d("pzn xx", _products.toString())

        try {
            for (i in 0 until _products.length()) {
                val jsonObject = _products.getJSONObject(i)
                val dataField1 = jsonObject.getString("prd_id").toInt()
                val dataField2 = jsonObject.getString("prd_nama").toString()
                val dataField3 = jsonObject.getString("prd_harga").toInt()
                val dataField4 = jsonObject.getString("prd_foto").toString()
                val dataField5 = jsonObject.getString("prd_deskripsi").toString()
                val data = Product(dataField1, dataField2, dataField3, dataField4, dataField5)
                productArray.add(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return productArray
    }
}