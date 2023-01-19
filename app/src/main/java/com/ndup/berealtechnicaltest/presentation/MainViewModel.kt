package com.ndup.berealtechnicaltest.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.logging.ApiModelObject
import com.ndup.berealtechnicaltest.repository.IRepository
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

data class MainModel(
    val currentUser: User?,
    val currentPath: List<Item>,
    val items: Items,
)

interface IMainListener {
    @Composable
    fun getModelAsState(): State<MainModel>

    fun onItemSelected(item: Item)
    fun onBack(): Boolean
    fun insertNewFolder(folderName: String)
    fun onItemDeletionRequest(item: Item)
    fun onLoggingRequest(name: String, password: String)
}

class MainViewModel @AssistedInject constructor(
    private val initialModel: MainModel,
    private val repository: IRepository,
) : ViewModel(),
    IMainListener {

    /**
     * Custom factory allowing the Activity to create VM with param
     */
    @AssistedFactory
    fun interface VMAssistedFactory {
        operator fun invoke(): MainViewModel
    }

    class Factory(
        private val assistedFactory: VMAssistedFactory,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = assistedFactory() as T
    }

    private val mutableModel: MutableModel by lazy { MutableModel(initialModel) }

    class MutableModel(private val initialModel: MainModel) : MutableLiveData<MainModel>(initialModel) {

        // value is nullable... this is making me crazy ><
        val nonNullValue: MainModel get() = value ?: initialModel

        // I don't like this pattern.
        // I wanted to do something cool non mutable but finally this is not as cool as I expected.
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

    @Composable
    override fun getModelAsState() = mutableModel.observeAsState(initial = initialModel)

    override fun onItemSelected(item: Item) {
        viewModelScope.launch {
            val folderContent = repository.getFolderContent(item.id)
            mutableModel.updateModel(
                items = folderContent,
                currentPath = mutableModel.nonNullValue.currentPath.plus(item).toSet().toList()
            )
        }
    }

    override fun onBack(): Boolean {
        val currentModel = mutableModel.value!!
        if (currentModel.currentPath.isEmpty()) return false

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
        return true
    }

    override fun insertNewFolder(folderName: String) {
        val tag = "INSERTING FOLDER"
        val currentModel = mutableModel.value!!
        val currentFolder = currentModel.currentPath.lastOrNull() ?: run {
            Log.e(tag, "Not able to add a new folder here. Not parent.")
            return
        }
        viewModelScope.launch {
            val createdItem = repository.createNewItem(currentFolder.id, folderName)
            Log.i(tag, "createdItem=$createdItem")
            onItemSelected(currentFolder)
        }
    }

    override fun onItemDeletionRequest(item: Item) {
        val tag = "DELETING FOLDER"
        Log.i(tag, "delete ${item.id}")
        val currentModel = mutableModel.value!!
        val currentFolder = currentModel.currentPath.lastOrNull() ?: run {
            Log.e(tag, "Deleting root is not allowed")
            return
        }
        viewModelScope.launch {
            repository.deleteItem(item.id)
            Log.i(tag, "${item.id} deleted successfully")
            onItemSelected(currentFolder)
        }
    }

    override fun onLoggingRequest(name: String, password: String) {
        ApiModelObject.updateCredentials(name, password)
        getCurrentUser()
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
