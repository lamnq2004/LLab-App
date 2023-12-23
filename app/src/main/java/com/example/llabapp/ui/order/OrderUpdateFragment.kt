package com.example.llabapp.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.llabapp.R
import com.example.llabapp.databinding.FragmentOrderUpdateBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderUpdateFragment : Fragment() {
    private var _binding: FragmentOrderUpdateBinding? = null
    private val binding get() = _binding!!
    private var currentOrder: Order? = null
    private val viewModel: OrderViewModel by viewModels()
    private var selectedBranch: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderId = requireArguments().getString("orderId")

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val expiryDateString = binding.pickupDateButton.text.toString()
        val expiryDateDate: Date? = try {
            sdf.parse(expiryDateString)
        } catch (e: ParseException) {
            null
        }

        val expiryTimestamp = expiryDateDate?.let { Timestamp(it) }

        viewModel.fetchOrderById(orderId.toString())

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.branch_names,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.branchButton.adapter = adapter
        }

        viewModel.selectedOrder.observe(viewLifecycleOwner) { order ->
            order?.let {
                binding.customerName.setText(order.customerName)
                binding.customerPhoneNumber.setText(order.phoneNumber)
                binding.customerEmail.setText(order.customerEmail)
                binding.totalRoll.setText(order.totalRoll.toString())

                val expiryDate: Date? = order.expiryDate?.toDate()
                val expiryDateString = expiryDate?.let { sdf.format(it) } ?: ""
                binding.pickupDateButton.setText(expiryDateString)

                currentOrder = order
                Log.d("CHECK_DATA", "Customer Name set to: ${order.customerName}")
            }

            val spinnerAdapter = binding.branchButton.adapter as ArrayAdapter<String>
            val position = spinnerAdapter.getPosition(order?.branch)
            binding.branchButton.setSelection(position)
        }


        binding.branchButton.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedBranch = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedBranch = ""
            }
        }

        binding.pickupDateButton.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.addOnPositiveButtonClickListener {
                val dateString = sdf.format(Date(it))
                binding.pickupDateButton.text = dateString
            }

            datePicker.show(childFragmentManager, datePicker.toString())
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveButton.setOnClickListener {
            val updatedOrder = Order(
                customerName = binding.customerName.text.toString(),
                customerEmail = binding.customerEmail.text.toString(),
                phoneNumber = binding.customerPhoneNumber.text.toString(),
                totalRoll = binding.totalRoll.text.toString().toInt(),
                expiryDate =  expiryTimestamp,
                orderNumber = currentOrder?.orderNumber?: "",
                orderId = currentOrder?.orderId?: "",
                branch = selectedBranch,
                orderStatus = currentOrder?.orderStatus?:""
            )

            Log.d("CHECK_DATA", "Updated Order Number ${currentOrder?.orderNumber}")
            viewModel.updateOrder(orderId.toString(), updatedOrder) { success ->
                if (success) {
                    Toast.makeText(context, "Order updated successfully.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Failed to update order.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this order?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteOrder(orderId.toString()) { success ->
                        if (success) {
                            Toast.makeText(context, "Order deleted successfully.", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        } else {
                            Toast.makeText(context, "Failed to delete order.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .show()
        }
    }
}