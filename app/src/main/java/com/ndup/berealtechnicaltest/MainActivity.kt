package com.ndup.berealtechnicaltest

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.presentation.IMainListener
import com.ndup.berealtechnicaltest.presentation.MainViewModel
import com.ndup.berealtechnicaltest.ui.ErrorLayout
import com.ndup.berealtechnicaltest.ui.FullScreenImage
import com.ndup.berealtechnicaltest.ui.ItemGrid
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
                println(currentUser)
                currentUser
                    ?.let {
                        ActivityMainLayout(
                            mainUser = it,
                            items = items,
                            currentPath = currentPath,
                        )
                    }
                    ?: ErrorLayout()
            }
        }

        onBackPressedDispatcher.addCallback {
            if (!mainListener.onBack()) finish()
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
            val requestedIdToDelete: MutableState<Item?> = remember { mutableStateOf(null) }
            Scaffold(
                modifier = modifier
                    .fillMaxHeight(),
                containerColor = Color.Black,
                floatingActionButton = {
                    if (currentPath.lastOrNull()?.isDir == true) FloatingActionButton(
                        // TODO NDU: currently assigning folder name automatically
                        // I'll probably use a TextField if I have time
                        onClick = { mainListener.insertNewFolder("NDU${System.currentTimeMillis()}") },
                        containerColor = Color.White,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_new_folder),
                            colorFilter = ColorFilter.tint(Color.Black),
                            contentDescription = "Add a new folder",
                            contentScale = ContentScale.Crop,
                        )
                    }
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
                    requestedIdToDelete.value?.let { itemToBeDeleted ->
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
                }
            )
        }
    }

}