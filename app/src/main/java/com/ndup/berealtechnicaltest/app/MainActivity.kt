package com.ndup.berealtechnicaltest.app

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.ndup.berealtechnicaltest.R
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.presentation.IMainListener
import com.ndup.berealtechnicaltest.presentation.MainViewModel
import com.ndup.berealtechnicaltest.ui.ErrorLayout
import com.ndup.berealtechnicaltest.ui.FullScreenImage
import com.ndup.berealtechnicaltest.ui.ItemGrid
import com.ndup.berealtechnicaltest.ui.LoggingLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalMaterial3Api
@OptIn(ExperimentalUnitApi::class)
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModel.VMAssistedFactory

    private val mainListener: IMainListener by viewModels {
        MainViewModel.Factory(mainViewModelFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            with(mainListener.getModelAsState().value) {
                error?.let {
                    ErrorLayout(it) { mainListener.onErrorDismissed() }
                    return@setContent
                }
                currentUser
                    ?.let {
                        ActivityMainLayout(
                            mainUser = it,
                            items = items,
                            currentPath = currentPath,
                        )
                    }
                    ?: LoggingLayout(
                        modifier = Modifier.padding(26.dp),
                        onLoggingValidate = { name, password -> mainListener.onLoggingRequest(name, password) }
                    )
            }
        }

        onBackPressedDispatcher.addCallback {
            if (!mainListener.onBack()) finish()
        }
    }

    private val resultsForImagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri = result.data?.data ?: run {
                Log.e("FAILURE", "Cannot decode image")
                return@registerForActivityResult
            }
            contentResolver.openInputStream(selectedImageUri)
                ?.let { mainListener.uploadStream(it) }
                ?: Log.e("ERROR", "Cannot openInputStream($selectedImageUri)")
        }
    }

    @Composable
    private fun ActivityMainLayout(
        modifier: Modifier = Modifier,
        mainUser: User,
        items: Items,
        currentPath: List<Item>,
    ) {
        MaterialTheme {
            val isRequestingFolderCreation: MutableState<Boolean> = remember { mutableStateOf(false) }
            val requestedIdToDelete: MutableState<Item?> = remember { mutableStateOf(null) }
            Scaffold(
                modifier = modifier
                    .fillMaxHeight(),
                containerColor = Color.Black,
                floatingActionButton = {
                    if (currentPath.lastOrNull()?.isDir == true) AddButtons(isRequestingFolderCreation)
                },
                content = { contentPadding ->
                    Column(
                        modifier = modifier
                            .fillMaxHeight()
                            .padding(contentPadding),
                        verticalArrangement = Arrangement.spacedBy(26.dp),
                    ) {
                        val rootUser = "${mainUser.firstName.take(1)}${mainUser.lastName}".lowercase()
                        Text(
                            text = "~/$rootUser${currentPath.joinToString(separator = "/", prefix = "/") { it.name }}",
                            color = Color.White,
                            fontSize = TextUnit(13f, TextUnitType.Sp),
                        )
                        currentPath.lastOrNull()
                            ?.takeUnless { it.isDir }
                            ?.let { FullScreenImage(item = it) }
                            ?: ItemGrid(
                                items,
                                Modifier.fillMaxHeight(),
                                3,
                                onItemSelected = mainListener::onItemSelected,
                                onItemLongPressed = { item ->
                                    requestedIdToDelete.value = item.takeUnless { it.id == "4b8e41fd4a6a89712f15bbf102421b9338cfab11" }
                                },
                            )
                    }
                    if (isRequestingFolderCreation.value) FolderCreationDialog(isRequestingFolderCreation)
                    requestedIdToDelete.value?.let { itemToBeDeleted -> DeletionDialog(itemToBeDeleted, requestedIdToDelete) }
                }
            )
        }
    }

    @Composable
    private fun DeletionDialog(
        itemToBeDeleted: Item,
        requestedIdToDelete: MutableState<Item?>
    ) {
        AlertDialog(
            confirmButton = {
                Button(onClick = {
                    mainListener.onItemDeletionRequest(itemToBeDeleted)
                    requestedIdToDelete.value = null
                }) { Text(text = "Yes") }
            },
            dismissButton = {
                Button(onClick = { requestedIdToDelete.value = null }) { Text(text = "No") }
            },
            onDismissRequest = {
                requestedIdToDelete.value = null
            },
            text = { Text(text = "Delete ${itemToBeDeleted.name} ?") }
        )
    }

    @Composable
    private fun FolderCreationDialog(isRequestingFolderCreation: MutableState<Boolean>) {
        var itemTitle by remember { mutableStateOf(TextFieldValue("")) }
        AlertDialog(
            confirmButton = {
                Button(onClick = {
                    isRequestingFolderCreation.value = false
                    mainListener.insertNewFolder(itemTitle.text)
                }) { Text(text = "Confirm") }
            },
            dismissButton = {
                Button(onClick = { isRequestingFolderCreation.value = false }) { Text(text = "Cancel") }
            },
            onDismissRequest = {
                isRequestingFolderCreation.value = false
            },
            text = {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = itemTitle,
                    onValueChange = { itemTitle = it },
                    placeholder = { Text(text = "Enter a title") },
                )
            }
        )
    }

    @Composable
    private fun AddButtons(isRequestingFolderCreation: MutableState<Boolean>) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(100.dp)),
                onClick = { isRequestingFolderCreation.value = true },
                containerColor = Color.White,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_new_folder),
                    colorFilter = ColorFilter.tint(Color.Black),
                    contentDescription = "Create folder",
                    contentScale = ContentScale.Crop,
                )
            }
            FloatingActionButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(100.dp)),
                onClick = {
                    println("Upload image")
                    resultsForImagePicker.launch(
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                            type = "image/*"
                        }
                    )
                },
                containerColor = Color.White,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_new_image),
                    colorFilter = ColorFilter.tint(Color.Black),
                    contentDescription = "Upload image",
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }

}