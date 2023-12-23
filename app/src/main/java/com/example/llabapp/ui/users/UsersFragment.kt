package com.example.llabapp.ui.users

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.llabapp.databinding.FragmentUsersBinding
import com.google.android.material.snackbar.Snackbar

class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel : UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editButton : Button = binding.editButton

        editButton.setOnClickListener {
            navigateEditActivity()
        }

        userViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

        userViewModel.userDetail.observe(viewLifecycleOwner){users ->
            if(users.isNotEmpty()){
                val user = users[0]
                updateUI(user)
            }
        }
        userViewModel.fetchUserDetail()
    }

    private fun updateUI(index : User){
        binding.userName.text = index.userName
        binding.userNameHeading.text = index.userName
        binding.phoneNumber.text = index.phoneNumber
        binding.userEmail.text = index.userEmail
    }

    private val updateForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val updatedName = result.data?.getStringExtra("name")
            val updatedEmail = result.data?.getStringExtra("email")
            val updatedPhone = result.data?.getStringExtra("phone")
            val userID = result.data?.getStringExtra("userID")

            val updatedUser = User(
                id = userID.toString(),
                userName = updatedName.toString(),
                userEmail = updatedEmail.toString(),
                phoneNumber = updatedPhone.toString(),
            )

            userViewModel.updateUser(updatedUser)

            updateUI(updatedUser)

            val rootLayout: ConstraintLayout = binding.fragmentUsers
            val snackBar = Snackbar.make(rootLayout, "Details Updated!", Snackbar.LENGTH_SHORT)
            snackBar.show()

        } else {
            Toast.makeText(activity, "Update Canceled", Toast.LENGTH_SHORT).show()

        }
    }

    private fun navigateEditActivity() {
        val user = userViewModel.userDetail.value?.get(0)
        user?.let {
            val intent = Intent(activity, UsersUpdateActivity::class.java)
            intent.putExtra("name", it.userName)
            intent.putExtra("email", it.userEmail)
            intent.putExtra("phone", it.phoneNumber)
            intent.putExtra("userID", it.id)
            updateForResult.launch(intent)
        }
    }
}