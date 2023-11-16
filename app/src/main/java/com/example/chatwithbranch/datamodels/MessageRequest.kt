package com.example.chatwithbranch.datamodels

import com.google.gson.annotations.SerializedName

data class MessageRequest(
    @SerializedName("thread_id") val threadId: Int,
    @SerializedName("body") val message: String
)
