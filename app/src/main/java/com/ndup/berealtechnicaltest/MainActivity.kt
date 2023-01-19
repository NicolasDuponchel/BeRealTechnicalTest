package com.ndup.berealtechnicaltest

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp),
        ) {
            val rootUser = "${mainUser.firstName.take(1)}${mainUser.lastName}".lowercase()
            Text(
                text = "~/$rootUser${currentPath.joinToString(separator = "/", prefix = "/") { it.name }}",
                color = Color.White,
            )
            currentPath.lastOrNull()
                ?.takeUnless { it.isDir }
                ?.let { FullScreenImage(item = it) }
                ?: ItemGrid(items, Modifier.fillMaxHeight(), 1) { mainListener.onItemSelected(it) }
        }
    }

}