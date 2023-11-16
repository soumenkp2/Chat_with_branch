package com.priyanshub.branchchat.domain.usecase

import android.util.Log
import androidx.paging.PagingSource
import com.example.chatwithbranch.data.repository.MessageServiceRepository
import com.example.chatwithbranch.datamodels.Message
import javax.inject.Inject

class MessageListUseCase @Inject constructor(
    private val repository: MessageServiceRepository
) {
    /**
     * Invokes the retrieval of messages using the provided authentication token [authToken].
     *
     * @param authToken The authentication token for accessing messages.
     * @return A PagingSource<Int, Message> representing paginated messages.
     */
    suspend operator fun invoke(authToken: String): PagingSource<Int,Message> {
        val pageList = repository.getMessages(authToken)
        return try {
            pageList
        } catch (e: Exception) {
            e.printStackTrace()
            pageList
        }
    }


}