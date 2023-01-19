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
    val currentPath: List<Item> = emptyList(),
    val items: Items = emptyList(),
)

interface IMainListener {
    @Composable
    fun getModelAsState(): State<MainModel>

    fun onItemSelected(item: Item)
    fun onBack()
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
            currentPath: List<Item>? = null,
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
        if (item.isDir) viewModelScope.launch {
            val folderContent = repository.getFolderContent(item.id)
            val currentModel = mutableModel.value!!
            mutableModel.postValue(
                currentModel.copy(
                    items = folderContent,
                    currentPath = currentModel.currentPath.plus(item)
                )
            )
        }
        else println("Not a folder, will handle image case next")
    }

    override fun onBack() {
        val currentModel = mutableModel.value!!
        if (currentModel.currentPath.isEmpty()) return // TODO NDU: do finish() in activity

        val newPath = currentModel.currentPath.dropLast(1)

        newPath.lastOrNull()
            ?.let { previousItem ->
                viewModelScope.launch {
                    val newItems = repository.getFolderContent(previousItem.id)
                    mutableModel.postValue(
                        currentModel.copy(
                            currentPath = newPath,
                            items = newItems,
                        )
                    )
                }
            }
            ?: mutableModel.postValue(
                currentModel.copy(
                    currentPath = newPath,
                    items = listOfNotNull(currentModel.currentUser?.rootItem),
                )
            )

    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            val currentUser = repository.getCurrentUser()
            mutableModel.updateModel(
                currentUser = currentUser,
                items = listOfNotNull(currentUser.rootItem),
            )
        }
    }

}
