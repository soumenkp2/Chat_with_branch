package com.example.chatwithbranch.presentation.message

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatwithbranch.databinding.ItemMessageLayoutBinding
import com.example.chatwithbranch.datamodels.Message
import java.text.SimpleDateFormat
import java.util.Locale

class MessagePagingAdapter(private val listener: ItemClickListener) : PagingDataAdapter<Message, MessagePagingAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageLayoutBinding.inflate(inflater, parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        message?.let {
            holder.binding.apply {
                tvThreadId.text = it.thread_id.toString()
                Log.d("soumen",it.body.toString())
                tvUserId.text = spannableString("User Id",message.user_id.toString())
                tvMsgBody.text = message.body
                tvTimeStamp.text = convertDateTime(message.timestamp)
                holder.itemView.setOnClickListener {
                    listener.onItemClick(message)
                }
            }
        }
    }

    class MessageViewHolder(val binding: ItemMessageLayoutBinding) : RecyclerView.ViewHolder(binding.root) {}

    // DiffCallback to efficiently update items in the RecyclerView
    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.thread_id == newItem.thread_id // Use a unique identifier for items
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem // Check if the items have the same content
        }
    }

    private fun convertDateTime(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a", Locale.getDefault())

        val date = inputFormat.parse(dateTimeString)
        return date?.let { outputFormat.format(it) }.toString()
    }

    private fun spannableString(heading : String, content : String): SpannableString {
        val string = SpannableString("$heading - $content")
        string.setSpan(StyleSpan(Typeface.BOLD), 0, heading.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        string.setSpan(ForegroundColorSpan(Color.BLACK), 0, heading.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return string
    }
}

interface ItemClickListener {
    fun onItemClick(message: Message)
}

