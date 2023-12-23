package com.example.llabapp.ui.order

import com.google.firebase.Timestamp


data class Order(
    var orderId : String = "",
    var branch: String = "",
    var expiryDate: Timestamp? = null,
    var orderNumber: String = "",
    var phoneNumber : String = "",
    var customerName : String = "",
    var customerEmail : String = "",
    var orderStatus: String = "",
    var totalRoll: Int = 0
)
