package com.example.gmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gmart.adapter.ProductsAdapter
import com.example.gmart.dataClass.Product
import com.example.gmart.databinding.FragmentProductsBinding
import com.example.gmart.`object`.Products

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit  var _binding: FragmentProductsBinding
    private val binding get() = _binding

    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var productArrayList : ArrayList<Product>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)

        productArrayList = Products.getArrayList()
        Log.d("pzn live 1",productArrayList.toString())

        val layoutManager = GridLayoutManager(context, 2)
        recyclerView = binding.productsFragmentRecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
//        productsAdapter = ProductsAdapter(productArrayList)
//        recyclerView.adapter = productsAdapter

        Products.liveProduct.observe(viewLifecycleOwner, {
            productArrayList = Products.getArrayList()

            productsAdapter = ProductsAdapter(productArrayList)
            recyclerView.adapter = productsAdapter

            productsAdapter.onItemClick = {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("prdInfo",it)
                startActivity(intent)
            }
        })






        return binding.root
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}