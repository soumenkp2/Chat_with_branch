package com.example.chatwithbranch.presentation.conversation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.Pager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatwithbranch.databinding.FragmentConversationBinding
import com.example.chatwithbranch.datamodels.Message
import com.example.chatwithbranch.datamodels.MessageRequest
import com.example.chatwithbranch.presentation.message.ItemClickListener
import com.example.chatwithbranch.presentation.message.MessageListFragmentDirections
import com.example.chatwithbranch.presentation.message.MessagePagingAdapter
import com.example.chatwithbranch.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment responsible for displaying and interacting with a conversation thread.
 * Manages the UI, message retrieval, and sending messages.
 */
@AndroidEntryPoint
class ConversationFragment : Fragment() {

    private lateinit var binding: FragmentConversationBinding
    private val viewModel: ConversationViewModel by viewModels()
    private var firstFetch = true
    private lateinit var conversationPagingAdapter: ConversationPagingAdapter
    private val args : ConversationFragmentArgs by navArgs()
    private lateinit var sharedPreference : SharedPreferences
    private var token : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentConversationBinding.inflate(layoutInflater,container,false)
        sharedPreference =  requireActivity().getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        token = sharedPreference.getString(AppConstants.TOKEN,"")
        viewModel.getMessagesForThreadId(token.toString(),args.threadId)

        setupRecyclerView()
        initObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreference =  requireActivity().getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = sharedPreference.getString(AppConstants.TOKEN,"")

        conversationPagingAdapter = ConversationPagingAdapter()


        binding.btnSend.setOnClickListener {
            val snackbar = AppConstants.snackBarTemplate(binding.root,"Sending Message....")
            snackbar.show()
            if (!binding.etReply.text.isNullOrBlank()){
                val messageRequest = MessageRequest(args.threadId, binding.etReply.text.toString())
                viewModel.sendMessage(token.toString(),messageRequest)
                binding.etReply.setText("")
                snackbar.dismiss()
            }
        }

        binding.ivReset.setOnClickListener {
            viewModel.reset(token.toString())
        }
    }

    /**
     * Initializes observers for LiveData updates from the ViewModel.
     */
    private fun initObservers() {

        viewModel.pagingData.observe(viewLifecycleOwner, Observer {
            conversationPagingAdapter.submitData(lifecycle,it)
            if(firstFetch) {
                binding.rvConvoThread.adapter = conversationPagingAdapter
                firstFetch = false
            }
            conversationPagingAdapter.notifyDataSetChanged()
        })

        viewModel.getMessage.observe(viewLifecycleOwner){message->
            val token = sharedPreference.getString(AppConstants.TOKEN,"")
            viewModel.getMessagesForThreadId(token.toString(),args.threadId)
        }

        viewModel.showProgress.observe(viewLifecycleOwner){showProgress->
            binding.progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
        }

        viewModel.isReset.observe(viewLifecycleOwner){
            if (it) {
                viewModel.getMessagesForThreadId(token.toString(),args.threadId)
                Toast.makeText(requireContext(), "Cleared Agent's chat", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Not able to reset", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvConvoThread.layoutManager = LinearLayoutManager(context)
    }
}