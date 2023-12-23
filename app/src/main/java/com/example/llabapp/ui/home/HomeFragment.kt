package com.example.llabapp.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.llabapp.R
import com.example.llabapp.databinding.FragmentHomeBinding
import com.example.llabapp.ui.order.OrderAdapter
import com.example.llabapp.ui.order.OrderViewModel
import com.example.llabapp.ui.users.User
import com.example.llabapp.ui.users.UsersViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderViewModel : OrderViewModel
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var userViewModel: UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar: Toolbar = root.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        //Disable title on the tool bar
        val activityCompat = activity as AppCompatActivity
        activityCompat.setSupportActionBar(toolbar)
        activityCompat.supportActionBar?.setDisplayShowTitleEnabled(false)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val allOrderButton : Button = view.findViewById(R.id.allOrder)
        val filmButton : Button = view.findViewById(R.id.filmButton)
        val serviceButton : Button = view.findViewById(R.id.serviceButton)
        val aboutButton : Button = view.findViewById(R.id.aboutButton)

        //Handle Button
        allOrderButton.setOnClickListener {
            navigateToOrderPage()
        }

        filmButton.setOnClickListener {
            navigateToOrderPage()
        }

        serviceButton.setOnClickListener {
            navigateToServiceWeb()
        }

        aboutButton.setOnClickListener {
            navigateToAbout()
        }

        orderAdapter = OrderAdapter(OrderAdapter.VIEW_TYPE_RECENT_ORDER){order ->
        }
        val recyclerView : RecyclerView = binding.recentOrderRecyclerView
        recyclerView.adapter = orderAdapter

        //Deactivate the scroll in RecyclerView
        val myLinearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        recyclerView.layoutManager = myLinearLayoutManager
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        orderViewModel.recentOrders.observe(viewLifecycleOwner) { recentOrderList ->
            orderAdapter.submitList(recentOrderList)
        }

        userViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

        userViewModel.userDetail.observe(viewLifecycleOwner){users ->
            if(users.isNotEmpty()){
                val user = users[0]
                updateWelcomeName(user)
            }
        }
        userViewModel.fetchUserDetail()
    }

    private fun updateWelcomeName(index : User){
        binding.welcomeName.text = index.userName
    }

    private fun navigateToOrderPage(){
        val action = HomeFragmentDirections.navigationToOrder()
        findNavController().navigate(action)
    }

    private fun navigateToServiceWeb(){
        val chromeIntent = CustomTabsIntent.Builder().build()
        chromeIntent.launchUrl(requireContext(), Uri.parse("https://llab.vn/en/service/"))
    }

    private fun navigateToAbout(){
        val chromeIntent = CustomTabsIntent.Builder().build()
        chromeIntent.launchUrl(requireContext(), Uri.parse("https://llab.vn/en/about-us/"))
    }
}