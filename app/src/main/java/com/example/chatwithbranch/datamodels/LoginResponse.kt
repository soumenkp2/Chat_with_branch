package com.example.chatwithbranch.datamodels

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("auth_token") val authToken: String
)
