package com.ndup.berealtechnicaltest

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.presentation.IMainListener
import com.ndup.berealtechnicaltest.presentation.MainViewModel
import com.ndup.berealtechnicaltest.repository.Repository
import com.ndup.berealtechnicaltest.repository.ServiceFactory
import com.ndup.berealtechnicaltest.ui.ErrorLayout
import com.ndup.berealtechnicaltest.ui.FullScreenImage
import com.ndup.berealtechnicaltest.ui.ItemGrid
import okhttp3.Credentials
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {

    // TODO NDU: store and get accessibility to this properly
    companion object {
        const val BaseUrl = "http://163.172.147.216:8080"
        const val UserName = "noel"
        const val UserPassword = "foobar"
    }

    // TODO NDU: do this properly with injection
    private val repo by lazy {
        Repository(
            ServiceFactory.service(
                baseUrl = BaseUrl,
                userName = UserName,
                userPassword = UserPassword,
                logLevel = HttpLoggingInterceptor.Level.BODY,
            )
        )
    }
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