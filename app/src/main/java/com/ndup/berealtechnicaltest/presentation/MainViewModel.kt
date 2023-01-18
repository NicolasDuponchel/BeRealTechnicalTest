package com.ndup.berealtechnicaltest.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.repository.IRepository
import kotlinx.coroutines.launch

data class MainModel(
    val currentUser: User?,
)

interface IMainListener {
    @Composable
    fun getModelAsState(): State<MainModel>
}

class MainViewModel(
    private val initialModel: MainModel = MainModel(null),
    private val repository: IRepository,
) : ViewModel(),
    IMainListener {

    private val mutableModel: MutableModel by lazy { MutableModel(initialModel) }

    class MutableModel(private val initialModel: MainModel) : MutableLiveData<MainModel>(initialModel) {

        private val nonNullValue: MainModel get() = value ?: initialModel

        fun updateCurrentUser(user: User) = postValue(nonNullValue.copy(currentUser = user))
    }

    init {
        getCurrentUser()
    }

    @Composable
    override fun getModelAsState() = mutableModel.observeAsState(initial = initialModel)

    private fun getCurrentUser() {
        Log.d("VIEW MODEL QUERY", "request current user")
        viewModelScope.launch {
            val currentUser = repository.getCurrentUser()
            Log.d("VIEW MODEL QUERY", "current user received: $currentUser")
            mutableModel.updateCurrentUser(currentUser)
        }
    }

}
