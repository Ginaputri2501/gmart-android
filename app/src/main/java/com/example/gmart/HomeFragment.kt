package com.example.gmart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gmart.databinding.FragmentHomeBinding
import com.example.gmart.`object`.HomeInfo
import com.example.gmart.`object`.KeranjangItems
import com.example.gmart.`object`.LoginSP
import com.example.gmart.`object`.Products


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit  var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root
    val loginInten = Intent(context, LoginActivity::class.java)

    textView = binding.tv

    val LoginSP = LoginSP.getSP()


    HomeInfo.liveInfoString.observe(viewLifecycleOwner, {
        textView.text = it
    })


//    binding.tvWelcome.setOnClickListener {
//        Log.d("pzn", Products.getArrayList().toString())
//        Log.d("pzn", Products.getArrayList().toString())
//        Log.d("pzn", LoginSP.getBoolean("status", false).toString())
//        Log.d("pzn",LoginSP.getString("userId", "null").toString())
//        startActivity(loginInten)
//    }

//        Mengambil data yang dikirim dari MainActivity
    val bundle = arguments
    Log.d("pzn", bundle.toString())

    binding.logo.setOnClickListener {
        Log.d("pzn keranjang", KeranjangItems.getkeranjangItemsArrayList().toString())
    }



    return  root
}

companion object {
    fun newInstance(param1: String, param2: String) =
        HomeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
}
}