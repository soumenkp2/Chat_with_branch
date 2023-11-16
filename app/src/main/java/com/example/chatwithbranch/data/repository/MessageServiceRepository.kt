package com.example.chatwithbranch.data.repository

import androidx.paging.PagingSource
import com.example.chatwithbranch.datamodels.LoginRequest
import com.example.chatwithbranch.datamodels.LoginResponse
import com.example.chatwithbranch.datamodels.Message
import com.example.chatwithbranch.datamodels.MessageRequest
import retrofit2.Response
import retrofit2.http.Header

interface MessageServiceRepository {
    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse>

    suspend fun getMessages(authToken: String): PagingSource<Int, Message>

    suspend fun getConversationMessages(authToken: String, threadId: Int) : PagingSource<Int, Message>

    suspend fun sendMessages(authToken: String, messageRequest: MessageRequest): Message

    suspend fun reset(@Header("X-Branch-Auth-Token") authToken: String)
}