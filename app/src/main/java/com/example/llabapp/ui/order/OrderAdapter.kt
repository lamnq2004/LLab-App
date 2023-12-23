package com.example.llabapp.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.llabapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(private val viewType: Int, private val onEditButtonClicked: (order: Order) -> Unit) : ListAdapter<Order, OrderAdapter.OrderViewHolder> (OrderDiffCallback()) {

    companion object{
        const val VIEW_TYPE_RECENT_ORDER = 1
        const val VIEW_TYPE_FULL = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutId = R.layout.order_component_full
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    inner class OrderViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val branchTextView : TextView = itemView.findViewById(R.id.location)
        private val expiryDateTextView : TextView = itemView.findViewById(R.id.expiryDate)
        private val orderNumberTextView : TextView = itemView.findViewById(R.id.orderNumber)
        private val totalRollTextView : TextView = itemView.findViewById(R.id.productQuantity)
        private val orderStatusTextView : TextView = itemView.findViewById(R.id.orderStatus)
        private val editButton : Button = itemView.findViewById(R.id.editButton)


        fun bind(order : Order){
            setTextView(order)
            handleEditButton(order)
            setStatusTextColor(order)
        }

        private fun setTextView(index : Order){
            branchTextView.text = "Branch: ${index.branch}"
            expiryDateTextView.text = "Pickup Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(index.expiryDate?.toDate())}"
            orderNumberTextView.text = index.orderNumber
            orderStatusTextView.text = index.orderStatus
            totalRollTextView.text = "Total rolls: ${index.totalRoll}"
        }

        private fun handleEditButton(index : Order){
            editButton.setOnClickListener {
                onEditButtonClicked(index)
            }
        }

        private fun setStatusTextColor(index : Order){
            val context = itemView.context
            when (index.orderStatus){
                "Processing" -> orderStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.purple))
                "Taken" -> orderStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.orange))
                "Destroyed" -> orderStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.red))
                "Expired" -> orderStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.grey))
                "Done" -> orderStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.green))
                "Created" -> orderStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.brown))
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}