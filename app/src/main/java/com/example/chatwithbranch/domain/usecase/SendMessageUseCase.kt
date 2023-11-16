package com.priyanshub.branchchat.domain.usecase

import com.example.chatwithbranch.data.repository.MessageServiceRepository
import com.example.chatwithbranch.datamodels.Message
import com.example.chatwithbranch.datamodels.MessageRequest
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: MessageServiceRepository
) {
    /**
     * Invokes the sending of a message using the provided authentication token [authToken] and [messageRequest].
     *
     * @param authToken The authentication token for sending the message.
     * @param messageRequest The request object containing details of the message to be sent.
     * @return A Message object representing the sent message, or a default Message on failure.
     */
    suspend operator fun invoke(authToken: String,messageRequest: MessageRequest): Message {
        return try {
            repository.sendMessages(authToken,messageRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            Message(0,0, timestamp = "")
        }
    }
}