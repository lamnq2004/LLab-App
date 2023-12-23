package com.example.llabapp.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.llabapp.databinding.FragmentNotificationDetailBinding

class NotificationsDetailFragment : Fragment() {
    private var _binding: FragmentNotificationDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel : NotificationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationId = requireArguments().getString("notificationId")
        Log.d("CHECK_DATA", notificationId.toString())
        viewModel.fetchNotificationById(notificationId.toString())

        viewModel.selectedNotification.observe(viewLifecycleOwner){notification ->
            notification?.let {
                binding.notificationTitle.setText(notification.title)
                binding.notificationDate.setText(notification.date)
                binding.notificationContent.setText(notification.content)
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}