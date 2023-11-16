package com.priyanshub.branchchat.domain.usecase

import com.example.chatwithbranch.data.repository.MessageServiceRepository
import javax.inject.Inject

class ResetUseCase @Inject constructor(
    private val repository: MessageServiceRepository
) {
    /**
     * Invokes the reset operation using the provided authentication token [authToken].
     *
     * @param authToken The authentication token for resetting.
     * @return `true` if the reset operation is successful, `false` otherwise.
     */
    suspend operator fun invoke(authToken: String) : Boolean{
        return try {
            repository.reset(authToken)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}