package com.example.chatwithbranch.data.apiservice

import com.example.chatwithbranch.datamodels.LoginRequest
import com.example.chatwithbranch.datamodels.LoginResponse
import com.example.chatwithbranch.datamodels.Message
import com.example.chatwithbranch.datamodels.MessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MessageApiService {
    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ) : Response<LoginResponse>

    @GET("api/messages")
    suspend fun getMessages(@Header("X-Branch-Auth-Token") authToken: String) : List<Message>

    @POST("api/messages")
    suspend fun sendMessage(
        @Header("X-Branch-Auth-Token") authToken: String,
        @Body request: MessageRequest
    ) : Message

    @POST("api/reset")
    suspend fun reset(@Header("X-Branch-Auth-Token") authToken: String)
}