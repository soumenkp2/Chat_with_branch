package com.priyanshub.branchchat.domain.usecase

import androidx.paging.PagingSource
import com.example.chatwithbranch.data.repository.MessageServiceRepository
import com.example.chatwithbranch.datamodels.Message
import javax.inject.Inject

class ConversationUseCase @Inject constructor(
    private val repository: MessageServiceRepository
) {
    /**
     * Asynchronous operator function to retrieve conversation messages as a PagingSource.
     *
     * @param authToken Authentication token for the API call.
     * @param threadId Identifier for the conversation thread.
     * @return PagingSource<Int, Message> representing paginated conversation messages.
     */
    suspend operator fun invoke(authToken: String, threadId: Int): PagingSource<Int, Message> {
        val pageList = repository.getConversationMessages(authToken,threadId)
        return try {
            pageList
        } catch (e: Exception) {
            e.printStackTrace()
            pageList
        }
    }
}