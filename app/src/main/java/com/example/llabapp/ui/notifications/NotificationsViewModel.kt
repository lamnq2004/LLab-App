package com.example.llabapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.llabapp.ui.order.Order
import com.example.llabapp.ui.order.OrderRepository

class NotificationsViewModel : ViewModel() {

    private val notificationRepository = NotificationRepository()

    private val _selectedNotification = MutableLiveData<Notification>()
    val selectedNotification: LiveData<Notification?> get() = _selectedNotification

    val notifications : LiveData<List<Notification>> get() = notificationRepository.notifications

    init {
        notificationRepository.fetchNotifications()
    }

    fun fetchNotificationById(id : String){
        notificationRepository.fetchNotificationsById(id){fetchedNotification ->
            _selectedNotification.value = fetchedNotification
        }
    }
}