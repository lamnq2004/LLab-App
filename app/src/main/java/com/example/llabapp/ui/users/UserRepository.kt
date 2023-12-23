package com.example.llabapp.ui.users

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class UserRepository {
    private val db = Firebase.firestore
    private val _userDetail = MutableLiveData<List<User>>()
    val userDetail : LiveData<List<User>> = _userDetail

    fun fetchUserDetail(){
        db.collection("user").get().addOnSuccessListener { snapshot ->
            val userList = snapshot.documents.map { document ->
                val user = document.toObject(User::class.java)!!
                user.copy(id = document.id)
            }
            _userDetail.value = userList
        }
    }

    fun updateUserInFirestore(updatedUser: User) {
        val userId = updatedUser.id
        db.collection("user").document(userId).set(updatedUser, SetOptions.merge())
    }
}