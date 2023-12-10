package com.example.gmart.`object`

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.gmart.dataClass.User
import org.json.JSONException

object LoggedInUser {
    var loggedInUser = User(null,"","","","")

    var _liveLoggedInUser = MutableLiveData<User>().apply {
        loggedInUser
    }

    val livaLoggedInUser : LiveData<User> = _liveLoggedInUser

    fun hapusUser (){
        loggedInUser = User(null,"","","","")
    }

    fun getUserInfo (id : String){

        val url = "https://wifi-map.my.id/api/gmart_get_user_info.php?user_id=$id"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val respon = response.getJSONObject(0)
                    val userId = respon.getString("us_id").toInt()
                    val username = respon.getString("us_username")
                    val nama = respon.getString("us_nama")
                    val tlp = respon.getString("us_tlp")
                    val alamat = respon.getString("us_alamat")
                    loggedInUser = User(userId, username, nama, tlp, alamat)
                    _liveLoggedInUser.postValue(User(userId, username, nama, tlp, alamat))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("err",error.toString())

            })

        RQ.getRQ().add(jsonArrayRequest)

    }

}