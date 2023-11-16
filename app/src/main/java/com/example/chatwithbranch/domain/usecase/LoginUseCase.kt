package com.priyanshub.branchchat.domain.usecase

import com.example.chatwithbranch.data.repository.MessageServiceRepository
import com.example.chatwithbranch.datamodels.LoginRequest
import com.example.chatwithbranch.datamodels.LoginResponse
import com.example.chatwithbranch.utils.AppResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: MessageServiceRepository
) {
    /**
     * Invokes the login operation using the provided [loginRequest].
     *
     * @param loginRequest The request object containing login credentials.
     * @return A Flow emitting AppResource, representing the loading state, success with response,
     *         or an error with an optional error message.
     */
    operator fun invoke(
        loginRequest: LoginRequest
    ): Flow<AppResource<Response<LoginResponse>>> = flow {
        try {
            emit(AppResource.Loading())
            val response = repository.login(loginRequest)
            emit(AppResource.Success(response))
        } catch (e: Exception)
        {
            e.printStackTrace()
            emit(AppResource.Error(e.localizedMessage ?: "An unexpected error occurred in login"))
        }
    }
}