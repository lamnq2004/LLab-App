package com.example.llabapp.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class UsersViewModel : ViewModel() {
    private val userRepository = UserRepository()

    val userDetail : LiveData<List<User>> get() = userRepository.userDetail

    fun fetchUserDetail() {
        userRepository.fetchUserDetail()
    }

    fun updateUser(updatedUser: User) {
        userRepository.updateUserInFirestore(updatedUser)
    }
}