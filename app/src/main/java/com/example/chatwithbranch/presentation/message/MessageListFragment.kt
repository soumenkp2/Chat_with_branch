package com.example.chatwithbranch.presentation.message

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatwithbranch.databinding.FragmentMessageListBinding
import com.example.chatwithbranch.datamodels.Message
import com.example.chatwithbranch.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment responsible for displaying a list of message threads.
 * Manages UI, message retrieval, navigation to conversation threads, and back press handling.
 */
@AndroidEntryPoint
class MessageListFragment : Fragment() {

    private val viewModel: MessageListViewModel by viewModels()
    private lateinit var binding: FragmentMessageListBinding
    lateinit var snackBarExitConfirmation: Snackbar
    private lateinit var pagingAdapter: MessagePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageListBinding.inflate(layoutInflater, container, false)

        val sharedPreference =  requireActivity().getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = sharedPreference.getString(AppConstants.TOKEN,"")

        pagingAdapter = MessagePagingAdapter(object : ItemClickListener {
            override fun onItemClick(message: Message) {
                val action = MessageListFragmentDirections.actionMessageThreadFragmentToConversationFragment(message.thread_id)
                findNavController().navigate(action)
            }
        })
        viewModel.getMessages(token.toString())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snackbar = AppConstants.snackBarTemplate(binding.root,"Fetching Thread....")
        snackbar.show()

        viewModel.pagingData.observe(viewLifecycleOwner, Observer {
            binding.rvMessageThread.layoutManager = LinearLayoutManager(context)
            pagingAdapter.submitData(lifecycle,it)
            binding.rvMessageThread.adapter = pagingAdapter
            pagingAdapter.notifyDataSetChanged()
            snackbar.dismiss()
        })

        viewModel.showProgress.observe(viewLifecycleOwner){showProgress->
            binding.paginationProgressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
        }

        binding.btnRetry.setOnClickListener {
            binding.btnRetry.visibility = View.GONE
            snackbar.show()
            val sharedPreference =  requireActivity().getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val token = sharedPreference.getString(AppConstants.TOKEN,"")
            viewModel.getMessages(token.toString())
        }
        setUpBackPress()
    }


    private fun setUpBackPress() {
        snackBarExitConfirmation = Snackbar.make(
            requireActivity().window.decorView.rootView,
            "Please press BACK again to exit",
            Snackbar.LENGTH_LONG
        )

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!snackBarExitConfirmation.isShown) {
                    snackBarExitConfirmation.show()
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}