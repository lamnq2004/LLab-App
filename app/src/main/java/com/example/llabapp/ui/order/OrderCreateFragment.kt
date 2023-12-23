package com.example.llabapp.ui.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.llabapp.R
import com.example.llabapp.databinding.FragmentOrderCreateBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderCreateFragment : Fragment() {
    private var _binding: FragmentOrderCreateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by viewModels()
    private var selectedBranch: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userDetail.observe(viewLifecycleOwner){users ->
            if(users.isNotEmpty()){
                val user = users[0]
                binding.customerName.setText(user.userName)
                binding.customerEmail.setText(user.userEmail)
                binding.customerPhoneNumber.setText(user.phoneNumber)
            }
        }

        viewModel.fetchUserDetail()


        binding.createButton.setOnClickListener {
            createOrder()
        }

        binding.createButton.setOnClickListener {
            createOrder()
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack(R.id.fragment_order, false)
        }

        binding.pickupDateButton.setOnClickListener {

            //Set date onwards
            val constraintsBuilder = CalendarConstraints.Builder()
            val dateValidatorMin = DateValidatorPointForward.now()
            constraintsBuilder.setValidator(dateValidatorMin)

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setTheme(R.style.CustomMaterialDatePicker)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateString = sdf.format(Date(it))
                binding.pickupDateButton.text = dateString
            }

            datePicker.show(childFragmentManager, datePicker.toString())
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.branch_names,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.branchButton.adapter = adapter
        }


        binding.branchButton.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedBranch = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedBranch = ""
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun createOrder() {

        val phoneNumberPattern = "^[0-9\\s]{8,12}$".toRegex()
        val namePattern = "^[A-Za-z ]{1,40}$".toRegex()
        val emailPattern = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*" +
                "@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()

        val name = binding.customerName.text.toString()
        val email = binding.customerEmail.text.toString()
        val phone = binding.customerPhoneNumber.text.toString()
        val totalRoll = binding.totalRoll.text.toString().toIntOrNull()

        if (name.isEmpty() || !name.matches(namePattern)) {
            Toast.makeText(context, "Please enter a valid name (Max 40 alpha characters).", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty() || !email.matches(emailPattern)) {
            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.isEmpty() || !phone.matches(phoneNumberPattern)) {
            Toast.makeText(context, "Please enter a valid phone number (8 to 12 digits or spaces).", Toast.LENGTH_SHORT).show()
            return
        }

        if (totalRoll == null || totalRoll <= 0) {
            Toast.makeText(context, "Please enter the total roll (greater than 0).", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // assuming the date format is "yyyy-MM-dd"
        val expiryDateString = binding.pickupDateButton.text.toString()
        val expiryDateDate: Date? = try {
            sdf.parse(expiryDateString)
        } catch (e: ParseException) {
            Toast.makeText(context, "Please select a valid date.", Toast.LENGTH_SHORT).show()
            return
        }


        val expiryTimestamp = expiryDateDate?.let { Timestamp(it) }

        val order = Order(
            customerName = binding.customerName.text.toString(),
            phoneNumber = binding.customerPhoneNumber.text.toString(),
            customerEmail = binding.customerEmail.text.toString(),
            expiryDate = expiryTimestamp,
            totalRoll = binding.totalRoll.text.toString().toInt(),
            branch = selectedBranch,
            orderStatus = "Created"
        )

        Log.d("DEBUG", "expiryDateString: $expiryDateString")
        Log.d("DEBUG", "expiryDateDate: $expiryDateDate")
        Log.d("DEBUG", "expiryTimestamp: $expiryTimestamp")


        viewModel.createOrder(order) { success ->
            if(success) {
                Toast.makeText(context, "Order Created with Order Number: ${order.orderNumber}", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Failed to create order.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}