package com.example.llabapp.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.llabapp.ui.users.User
import com.example.llabapp.ui.users.UserRepository

class OrderViewModel : ViewModel() {

    private val orderRepository = OrderRepository()

    private val userRepository = UserRepository()

    val userDetail : LiveData<List<User>> get() = userRepository.userDetail
    val orders : LiveData<List<Order>> get() = orderRepository.orders
    val recentOrders : LiveData<List<Order>> get() = orderRepository.recentOrders
    private val _selectedOrder = MutableLiveData<Order>()
    val selectedOrder: LiveData<Order?> get() = _selectedOrder

    init {
        orderRepository.fetchOrders()
        orderRepository.fetchRecentOrders()
    }

    fun createOrder(order: Order, onComplete: (Boolean) -> Unit) {
        orderRepository.createOrder(order, onComplete)
    }

    fun fetchOrderById(id: String) {
        orderRepository.fetchOrderById(id) { fetchedOrder ->
            _selectedOrder.value = fetchedOrder
        }
    }

    fun updateOrder(id: String, order: Order, onComplete: (Boolean) -> Unit) {
        orderRepository.updateOrder(id, order) {
            onComplete(true)
        }
    }

    fun deleteOrder(orderId: String, callback: (Boolean) -> Unit) {
        orderRepository.deleteOrder(orderId, callback)
    }

    fun fetchUserDetail() {
        userRepository.fetchUserDetail()
    }
}

