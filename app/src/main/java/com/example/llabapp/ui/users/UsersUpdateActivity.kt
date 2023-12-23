package com.example.llabapp.ui.users

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.llabapp.R

class UsersUpdateActivity : AppCompatActivity() {

    private lateinit var editName : EditText
    private lateinit var editPhoneNumber: EditText
    private lateinit var editMail : EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user_update)

        editName = findViewById(R.id.editName)
        editPhoneNumber = findViewById(R.id.editPhoneNumber)
        editMail = findViewById(R.id.editMail)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        val userID = intent.getStringExtra("userID")
        val currentName = intent.getStringExtra("name")
        val currentEmail = intent.getStringExtra("email")
        val currentPhoneNumber = intent.getStringExtra("phone")
        val currentPoints = intent.getIntExtra("currentPoint", 0)
        val usedPoints = intent.getIntExtra("usedPoint", 0)

        editName.setText(currentName)
        editPhoneNumber.setText(currentPhoneNumber)
        editMail.setText(currentEmail)

        saveButton.setOnClickListener {

            val updatedName = editName.text.toString()
            val updatedEmail = editMail.text.toString()
            val updatedPhoneNumber = editPhoneNumber.text.toString()

            val updatedUser = User(
                id = userID.toString(),
                userName = updatedName,
                userEmail = updatedEmail,
                phoneNumber = updatedPhoneNumber,
            )

            userRepository.updateUserInFirestore(updatedUser)


            val resultIntent = Intent().apply {
                putExtra("name", editName.text.toString())
                putExtra("email", editMail.text.toString())
                putExtra("phone", editPhoneNumber.text.toString())
                putExtra("userID", userID)
                putExtra("currentPoint", currentPoints)
                putExtra("usedPoint", usedPoints)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

    }
}