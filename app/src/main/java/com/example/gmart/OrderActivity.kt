package com.example.gmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gmart.adapter.OrderAdapter
import com.example.gmart.dataClass.Order
import com.example.gmart.databinding.ActivityOrderBinding
import com.example.gmart.`object`.LoggedInUser
import com.example.gmart.`object`.OrderDetail
import com.example.gmart.`object`.Orders

class OrderActivity : AppCompatActivity() {

    lateinit var binding : ActivityOrderBinding

    private lateinit var orderAdapter: OrderAdapter
    private lateinit var recyclerView: RecyclerView
    private var ordersArrayList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Orders.getFromDB(LoggedInUser.loggedInUser.id.toString())


        recyclerView = binding.orderRV
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        orderAdapter = OrderAdapter(ordersArrayList)
        recyclerView.adapter = orderAdapter

        Orders.liveOrders.observe(this, {
            orderAdapter.list = Orders.getArrayList()
            orderAdapter.notifyDataSetChanged()
        })

        orderAdapter.onItemClick = {
            Log.d("pzn order id", it.get(0))
            OrderDetail.getOrderDetailFromDB(it.get(0))
            val intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("prd_id",it.get(0))
            startActivity(intent)
        }

    }
}