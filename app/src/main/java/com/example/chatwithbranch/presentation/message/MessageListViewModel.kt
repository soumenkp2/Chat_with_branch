package com.example.chatwithbranch.presentation.message

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
import com.priyanshub.branchchat.domain.usecase.MessageListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val messageThreadUseCase: MessageListUseCase
) : ViewModel() {

    private val _showProgress : MutableLiveData<Boolean> = MutableLiveData()
    val showProgress: LiveData<Boolean>
        get() = _showProgress

    private val _getMessageResponse : MutableLiveData<List<Message>> = MutableLiveData()
    val getMessageResponse: MutableLiveData<List<Message>>
        get() = _getMessageResponse

    private val _pagingData = MutableLiveData<PagingData<Message>>()
    val pagingData: LiveData<PagingData<Message>> get() = _pagingData

    fun getMessages(authToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _showProgress.postValue(true)
        _showProgress.postValue(false)

        viewModelScope.launch {
            val messageSource = messageThreadUseCase.invoke(authToken)
            val pager = Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = { messageSource }
            )

            val pagingData = pager.flow.cachedIn(viewModelScope)
            pagingData.asLiveData().observeForever {
                _pagingData.postValue(it)
            }
        }


    }
}