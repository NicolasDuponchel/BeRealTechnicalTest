package com.ndup.berealtechnicaltest.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ndup.berealtechnicaltest.domain.Failure
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.Success
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.login.ApiModelObject
import com.ndup.berealtechnicaltest.repository.IRepository
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

data class MainModel(
    val currentUser: User?,
    val currentPath: List<Item>,
    val items: Items,
    val error: Throwable? = null,
)

interface IMainListener {
    @Composable
    fun getModelAsState(): State<MainModel>

    fun onItemSelected(item: Item)
    fun onBack(): Boolean
    fun insertNewFolder(folderName: String)
    fun onItemDeletionRequest(item: Item)
    fun onLoggingRequest(name: String, password: String)
    fun onErrorDismissed()
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
            when (val result = repository.getFolderContent(item.id)) {
                is Failure -> mutableModel.postValue(mutableModel.nonNullValue.copy(error = result.error))
                is Success -> mutableModel.updateModel(
                    items = result.value,
                    currentPath = mutableModel.nonNullValue.currentPath.plus(item).toSet().toList()
                )
            }
        }
    }

    override fun onBack(): Boolean {
        val currentModel = mutableModel.nonNullValue
        if (currentModel.currentPath.isEmpty()) return false

        val newPath = currentModel.currentPath.dropLast(1)

        newPath.lastOrNull()
            ?.let { previousItem ->
                viewModelScope.launch {
                    when (val result = repository.getFolderContent(previousItem.id)) {
                        is Failure -> mutableModel.postValue(mutableModel.nonNullValue.copy(error = result.error))
                        is Success -> mutableModel.postValue(
                            currentModel.copy(
                                currentPath = newPath,
                                items = result.value,
                            )
                        )
                    }
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
        val currentModel = mutableModel.nonNullValue
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
        val currentModel = mutableModel.nonNullValue
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

    override fun onErrorDismissed() {
        mutableModel.postValue(mutableModel.nonNullValue.copy(error = null))
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            when (val result = repository.getCurrentUser()) {
                is Failure -> mutableModel.postValue(mutableModel.nonNullValue.copy(error = result.error))
                is Success -> mutableModel.updateModel(
                    currentUser = result.value,
                    items = listOfNotNull(result.value.rootItem),
                )
            }
        }
    }

}
