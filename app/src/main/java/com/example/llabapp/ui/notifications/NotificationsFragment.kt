package com.example.llabapp.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.llabapp.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : NotificationsViewModel
    private lateinit var notificationsAdapter : NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView : RecyclerView = binding.notificationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        notificationsAdapter = NotificationsAdapter {selectedNotification ->
            val action = NotificationsFragmentDirections.actionNavigationNotificationToDetailedNotification(selectedNotification.id)
            findNavController().navigate(action)

            Log.d("CHECK_DATA", selectedNotification.id)
        }

        recyclerView.adapter = notificationsAdapter

        viewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        viewModel.notifications.observe(viewLifecycleOwner){notificationsList ->
            notificationsAdapter.submitList(notificationsList)
        }
    }
}