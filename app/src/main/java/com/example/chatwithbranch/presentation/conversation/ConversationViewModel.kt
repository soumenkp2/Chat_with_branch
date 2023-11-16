package com.example.chatwithbranch.presentation.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.chatwithbranch.datamodels.Message
import com.example.chatwithbranch.datamodels.MessageRequest
import com.priyanshub.branchchat.domain.usecase.ConversationUseCase
import com.priyanshub.branchchat.domain.usecase.ResetUseCase
import com.priyanshub.branchchat.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationUseCase: ConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val resetUseCase: ResetUseCase
) : ViewModel() {
    private val _showProgress: MutableLiveData<Boolean> = MutableLiveData()
    val showProgress: LiveData<Boolean>
        get() = _showProgress

    private val _showSentProgress: MutableLiveData<Boolean> = MutableLiveData()
    val showSentProgress: LiveData<Boolean>
        get() = _showSentProgress

    private val _getMessageResponse: MutableLiveData<List<Message>> = MutableLiveData()
    val getMessageResponse: MutableLiveData<List<Message>>
        get() = _getMessageResponse

    private val _getMessage: MutableLiveData<Message> = MutableLiveData()
    val getMessage: MutableLiveData<Message>
        get() = _getMessage

    private val _isReset: MutableLiveData<Boolean> = MutableLiveData()
    val isReset: MutableLiveData<Boolean>
        get() = _isReset

    private val _pagingData = MutableLiveData<PagingData<Message>>()
    val pagingData: LiveData<PagingData<Message>> get() = _pagingData

    fun getMessagesForThreadId(authToken: String, threadId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _showProgress.postValue(true)

            viewModelScope.launch {
                val messageSource = conversationUseCase.invoke(authToken,threadId)
                val pager = Pager(
                    config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                    pagingSourceFactory = { messageSource }
                )

                val pagingData = pager.flow.cachedIn(viewModelScope)
                pagingData.asLiveData().observeForever {
                    _pagingData.postValue(it)
                }
            }
            _showProgress.postValue(false)
        }

     fun sendMessage(authToken: String, messageRequest: MessageRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            val message = sendMessageUseCase.invoke(authToken, messageRequest)
            _getMessage.postValue(message)
        }

     fun reset(authToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _showSentProgress.postValue(true)
        val reset = resetUseCase.invoke(authToken)
        _isReset.postValue(reset)
        _showSentProgress.postValue(false)
    }


}