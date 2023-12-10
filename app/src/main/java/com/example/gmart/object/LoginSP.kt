package com.example.gmart.`object`

import android.content.Context
import android.content.SharedPreferences

object LoginSP {

    lateinit var LoginSP : SharedPreferences

    fun initLoginSP (context: Context){
        val sp = context.getSharedPreferences("loginSP", Context.MODE_PRIVATE)
        LoginSP = sp
    }

    fun getSP () : SharedPreferences {
        return LoginSP
    }

    fun getLoginStatus () : Boolean {
        return LoginSP.getBoolean("status", false)
    }

    fun hapusSP (){
        LoginSP.edit().putBoolean("status", false).apply()
        LoginSP.edit().putString("userId", null).apply()
    }


}
