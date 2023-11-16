package com.example.chatwithbranch.presentation.conversation

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
import com.example.chatwithbranch.databinding.ItemMessageAgentBinding
import com.example.chatwithbranch.databinding.ItemMessageLayoutBinding
import com.example.chatwithbranch.databinding.ItemMessageUserBinding
import com.example.chatwithbranch.datamodels.Message
import com.example.chatwithbranch.presentation.message.ItemClickListener
import java.text.SimpleDateFormat
import java.util.Locale

class ConversationPagingAdapter() : PagingDataAdapter<Message, RecyclerView.ViewHolder>(ConversationDiffCallback()) {

    private val VIEW_TYPE_USER = 0
    private val VIEW_TYPE_AGENT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemMessageUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return UserViewHolder(binding)
            }
            VIEW_TYPE_AGENT -> {
                val binding = ItemMessageAgentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                AgentViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        Log.d("so",position.toString())
        when (holder) {
            is UserViewHolder -> {
                if (message != null) {
                    holder.bind(message)
                }
            }
            is AgentViewHolder -> {
                if (message != null) {
                    holder.bind(message)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        if (message != null) {
            return if (message.agent_id == null) {
                VIEW_TYPE_USER
            } else {
                VIEW_TYPE_AGENT
            }
        } else {
            return -1
        }

    }


    // DiffCallback to efficiently update items in the RecyclerView
    private class ConversationDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.thread_id == newItem.thread_id // Use a unique identifier for items
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem // Check if the items have the same content
        }
    }

    fun convertDateTime(dateTimeString: String): String {
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

    inner class UserViewHolder( val binding: ItemMessageUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvUserId.text = message.user_id.toString()
            binding.tvBody.text = message.body.toString()
            binding.tvTime.text = convertDateTime(message.timestamp)
        }
    }

    inner class AgentViewHolder(val binding: ItemMessageAgentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvAgentId.text = message.agent_id.toString()
            binding.tvBody.text = message.body.toString()
            binding.tvTime.text = convertDateTime(message.timestamp)
        }
    }
}
