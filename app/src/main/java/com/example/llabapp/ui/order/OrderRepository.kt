package com.example.llabapp.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class OrderRepository {
    private val db = Firebase.firestore
    private val _orders = MutableLiveData<List<Order>>()
    val recentOrders : MutableLiveData<List<Order>> = MutableLiveData()
    val orders : LiveData<List<Order>> = _orders

    fun fetchOrders(){
        db.collection("order").addSnapshotListener { snapshot, error ->
            val orderList = snapshot?.documents?.map { docSnapshot ->
                val order = docSnapshot.toObject(Order::class.java)
                order?.orderId = docSnapshot.id
                order
            }?.filterNotNull() ?: listOf()

            if (error != null) {
                Log.e("OrderRepo", "Error fetching orders", error)
                return@addSnapshotListener
            }

            _orders.value = orderList
        }
    }

    fun fetchRecentOrders() {
        db.collection("order")
            .orderBy("orderNumber", Query.Direction.DESCENDING)
            .limit(2)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val orderList = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java) } ?: listOf()
                recentOrders.value = orderList
            }
    }

    fun createOrder(order: Order, onComplete: (Boolean) -> Unit) {
        db.runTransaction { transaction ->
            val countDocRef = db.collection("metadata").document("orderCount")
            val countDoc = transaction.get(countDocRef)

            val currentCount = countDoc.getLong("count") ?: 0L
            val newCount = currentCount + 1

            val orderNumber = String.format("%05d", newCount)

            order.orderNumber = orderNumber
            transaction.set(countDocRef, mapOf("count" to newCount), SetOptions.merge())
            transaction.set(db.collection("order").document(), order)

        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    private fun getRecentOrders() : Task<QuerySnapshot> {
        return db.collection("order")
            .orderBy(
                "orderNumber",
                Query.Direction.DESCENDING
            )
            .limit(2)
            .get()
    }

    fun fetchOrderById(id: String, callback: (Order) -> Unit) {
        db.collection("order").document(id).get().addOnSuccessListener { snapshot ->
            val order = snapshot.toObject(Order::class.java)
            order?.let { callback(it) }
        }
    }

    fun updateOrder(id: String, order: Order, callback: () -> Unit) {
        db.collection("order").document(id).set(order).addOnSuccessListener {
            callback()
        }
    }

    fun deleteOrder(orderId: String, callback: (Boolean) -> Unit) {
        db.collection("order").document(orderId)
            .delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}