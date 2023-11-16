package com.example.chatwithbranch.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.chatwithbranch.data.apiservice.MessageApiService
import com.example.chatwithbranch.datamodels.Message
import javax.inject.Inject

/**
 * A PagingSource implementation for loading paginated messages from a remote API.
 *
 * @property authToken The authentication token required for API requests.
 * @property apiService The API service responsible for fetching messages.
 * @property type The type of messages to retrieve, either "message" or conversation-specific.
 * @property threadId The ID of the conversation thread for fetching specific messages.
 */
class MessagePagingSource @Inject constructor(
    private val authToken : String, val apiService: MessageApiService, private val type: String, private val threadId : Int
) : PagingSource<Int, Message>() {

    private lateinit var sortedList : List<Message>
    val latestTimestampMap = HashMap<Int, String>()

    /**
     * Fetches paginated messages from the remote API based on the provided parameters.
     *
     * @param params The parameters for loading pages.
     * @return A LoadResult containing paginated data or an error.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getMessages(authToken)
            val messageList = response

            if(type.equals("message")) {
                getSorterMessageList(messageList)
            } else {
                getConversationMessageList(messageList)
            }

            LoadResult.Page(
                data = sortedList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (sortedList.isEmpty() || (page*params.loadSize)>=sortedList.size) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * Retrieves the refresh key for the current paging state.
     *
     * @param state The current PagingState.
     * @return The refresh key for the state.
     */
    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    /**
     * Filters and sorts the provided list to get messages specific to a conversation thread.
     *
     * @param messageList The list of messages to filter.
     */
    fun getConversationMessageList(messageList: List<Message>) {
        val filteredList: MutableList<Message> = mutableListOf()
        for(message in messageList) {
            if(message.thread_id == threadId) {
                filteredList.add(message)
            }
        }

        sortedList = filteredList.sortedBy { it.timestamp }
    }

    /**
     * Filters and sorts the provided list to get the latest messages for each thread.
     *
     * @param messageList The list of messages to filter.
     */
    fun getSorterMessageList(messageList : List<Message>) {
        for (message in messageList) {
            val threadId = message.thread_id
            val timestamp = message.timestamp

            if (!latestTimestampMap.containsKey(threadId) || timestamp > latestTimestampMap[threadId].toString()) {
                latestTimestampMap[threadId] = timestamp
            }
        }
        val filteredList = ArrayList<Message>()
        for (message in messageList) {
            val threadId = message.thread_id
            val timestamp = message.timestamp
            if (timestamp == latestTimestampMap[threadId]) {
                filteredList.add(message)
            }
        }
        sortedList = filteredList.sortedByDescending { it.timestamp }
    }
}