package com.example.llabapp.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.llabapp.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OrderViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding.addButton.setOnClickListener{
            val action = OrderFragmentDirections.actionNavigationOrderToOrderCreateFragment()
            findNavController().navigate(action)
        }

        val recyclerView : RecyclerView = binding.orderRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        orderAdapter = OrderAdapter(OrderAdapter.VIEW_TYPE_FULL) { selectedOrder ->
            val action = OrderFragmentDirections.actionNavigationOrderToOrderUpdateFragment(selectedOrder.orderId)
            findNavController().navigate(action)
        }

        recyclerView.adapter = orderAdapter

        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        viewModel.orders.observe(viewLifecycleOwner) { ordersList ->
            orderAdapter.submitList(ordersList)
        }
    }
}