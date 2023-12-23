package com.example.llabapp.ui.notifications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class NotificationRepository {
    private val db = Firebase.firestore
    private val _notifications = MutableLiveData<List<Notification>> ()
    val notifications : LiveData<List<Notification>> = _notifications

    fun fetchNotifications() {
        db.collection("notifications").addSnapshotListener { snapshot, error ->

            if (error != null) {
                Log.e("NotificationRepo", "Error fetching notifications", error)
                return@addSnapshotListener
            }

            val notificationList = snapshot?.documents?.map { docSnapshot ->
                val notification = docSnapshot.toObject(Notification::class.java)
                notification?.id= docSnapshot.id
                notification
            }?.filterNotNull() ?: listOf()

            _notifications.value = notificationList
        }
    }

    fun fetchNotificationsById(id: String, callback: (Notification) -> Unit) {
        db.collection("notifications").document(id).get().addOnSuccessListener {snapshot ->
            val notification = snapshot.toObject(Notification::class.java)
            if (notification == null) {
                Log.d("NotificationRepo", "Notification not found with ID: $id")
            } else {
                Log.d("NotificationRepo", "Fetched notification with ID: $id")
            }
            notification?.let {callback(it)}
        }
    }
}