package com.ndup.berealtechnicaltest.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.repository.IRepository
import kotlinx.coroutines.launch

data class MainModel(
    val currentUser: User? = null,
    val currentPath: List<String> = emptyList(),
    val items: Items = emptyList(),
)

interface IMainListener {
    @Composable
    fun getModelAsState(): State<MainModel>

    fun onItemSelected(item: Item)
}

class MainViewModel(
    private val initialModel: MainModel = MainModel(),
    private val repository: IRepository,
) : ViewModel(),
    IMainListener {

    private val mutableModel: MutableModel by lazy { MutableModel(initialModel) }

    class MutableModel(private val initialModel: MainModel) : MutableLiveData<MainModel>(initialModel) {

        // value is nullable... this is making me crazy ><
        private val nonNullValue: MainModel get() = value ?: initialModel

        // I don't like this pattern. Wanted to do something cool non mutable but finally this is not as cool as I expected.
        fun updateModel(
            currentUser: User? = null,
            currentPath: List<String>? = null,
            items: Items? = null,
        ) = postValue(
            nonNullValue.copy(
                currentUser = currentUser ?: nonNullValue.currentUser,
                currentPath = currentPath ?: nonNullValue.currentPath,
                items = items ?: nonNullValue.items,
            )
        )
    }

    init {
        getCurrentUser()
    }

    @Composable
    override fun getModelAsState() = mutableModel.observeAsState(initial = initialModel)

    override fun onItemSelected(item: Item) {
        viewModelScope.launch {
            val folderContent = repository.getFolderContent(item.id)
            val currentModel = mutableModel.value!!
            mutableModel.postValue(
                currentModel.copy(
                    items = folderContent,
                    currentPath = currentModel.currentPath.plus(item.name)
                )
            )
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            val currentUser = repository.getCurrentUser()
            mutableModel.updateModel(
                currentUser = currentUser,
                currentPath = listOf("${currentUser.firstName.take(1)}${currentUser.lastName}".lowercase()),
                items = listOfNotNull(currentUser.rootItem),
            )
        }
    }

}
