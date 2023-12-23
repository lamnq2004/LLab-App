package com.example.llabapp.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.llabapp.R

class NotificationsAdapter(private val onClick: (Notification) -> Unit) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    private var notificationsList: List<Notification> = emptyList()

    class NotificationViewHolder(itemView: View, val onClick: (Notification) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.notificationTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.notificationDescription)
        private val dateTextView: TextView = itemView.findViewById(R.id.notificationDate)
        private val cardView : CardView = itemView.findViewById(R.id.notificationCard)

        fun bind(notification: Notification) {
            titleTextView.text = notification.title
            descriptionTextView.text = notification.description
            dateTextView.text = notification.date
            cardView.setOnClickListener { onClick(notification) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val layoutId = R.layout.notification_component
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return NotificationViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationsList[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int = notificationsList.size

    fun submitList(notifications: List<Notification>) {
        notificationsList = notifications
        notifyDataSetChanged()
    }
}
