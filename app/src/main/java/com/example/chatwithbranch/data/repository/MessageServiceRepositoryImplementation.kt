package com.example.chatwithbranch.data.repository

import androidx.paging.PagingSource
import com.example.chatwithbranch.data.apiservice.MessageApiService
import com.example.chatwithbranch.datamodels.LoginRequest
import com.example.chatwithbranch.datamodels.LoginResponse
import com.example.chatwithbranch.datamodels.Message
import com.example.chatwithbranch.datamodels.MessageRequest
import com.example.chatwithbranch.paging.MessagePagingSource
import retrofit2.Response

class MessageServiceRepositoryImplementation(
    private val api: MessageApiService
): MessageServiceRepository {
    override suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return api.login(loginRequest)
    }

    override suspend fun getMessages(authToken: String): PagingSource<Int,Message> {
        return MessagePagingSource(authToken,api,"message",-1)
    }

    override suspend fun getConversationMessages(authToken: String, threadId: Int): PagingSource<Int,Message> {
        return MessagePagingSource(authToken,api,"conversation",threadId)
    }

    override suspend fun sendMessages(authToken: String, messageRequest: MessageRequest): Message {
        return api.sendMessage(authToken, messageRequest)
    }

    override suspend fun reset(authToken: String) {
        return api.reset(authToken)
    }

}