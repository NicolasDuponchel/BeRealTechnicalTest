package com.ndup.berealtechnicaltest

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.presentation.IMainListener
import com.ndup.berealtechnicaltest.presentation.MainViewModel
import com.ndup.berealtechnicaltest.repository.Repository
import com.ndup.berealtechnicaltest.repository.ServiceFactory

class MainActivity : AppCompatActivity() {

    private val repo by lazy { Repository(ServiceFactory.service) }
    private val mainListener: IMainListener by lazy { MainViewModel(repository = repo) }

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
        Column {
            val rootUser = "${mainUser.firstName.take(1)}${mainUser.lastName}".lowercase()
            Text(
                text = "~/$rootUser${currentPath.joinToString(separator = "/", prefix = "/") { it.name }}",
                color = Color.White,
            )
            ItemGrid(items, Modifier.fillMaxHeight(), 1) { mainListener.onItemSelected(it) }
        }
    }


    @Composable
    fun ItemGrid(
        list: Items,
        modifier: Modifier = Modifier,
        initialColumnCount: Int = 2,
        onItemSelected: (section: Item) -> Unit = {},
    ) {
        check(initialColumnCount > 0)
        val padding = 12f
        val screenWidth = LocalConfiguration.current.screenWidthDp.toFloat()
        val initialItemWidthFactorised = screenWidth.div(initialColumnCount) - padding.times(2)
        val arrangementWidth by remember { mutableStateOf(initialItemWidthFactorised) }
        val arrangement = Arrangement.spacedBy(padding.dp)

        LazyVerticalGrid(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = arrangement,
            horizontalArrangement = arrangement,
            contentPadding = PaddingValues(padding.dp),
            columns = GridCells.Adaptive(arrangementWidth.dp),
        ) {
            items(items = list) {
                Text(
                    text = it.name,
                    color = Color.White,
                    modifier = Modifier
                        .width(arrangementWidth.dp)
                        .clickable { onItemSelected(it) },
                )
            }
        }
    }

    @Composable
    private fun ErrorLayout() {
        Text(
            text = "No user identified :(",
            color = Color.White,
        )
    }

}