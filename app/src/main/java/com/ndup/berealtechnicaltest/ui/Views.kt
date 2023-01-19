package com.ndup.berealtechnicaltest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.ndup.berealtechnicaltest.MainActivity
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import okhttp3.Credentials


@Composable
fun FullScreenImage(
    modifier: Modifier = Modifier,
    item: Item,
) {
    val imageUrl = "${MainActivity.BaseUrl}/items/${item.id}/data".also { println("download image at $it") }
    val roundedShape = RoundedCornerShape(6.dp)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color.Black,
                shape = roundedShape,
            )
            .shadow(
                elevation = 4.dp,
                shape = roundedShape
            )
            .clip(roundedShape),
        contentAlignment = Alignment.Center,
    ) {
        SubcomposeAsyncImage(
            modifier = modifier.fillMaxSize(),
            model = crossFade(imageUrl = imageUrl),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
        ) {
            when (painter.state) {
                AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Error -> Text(
                    text = "NOT able to load image at $imageUrl",
                    color = Color.White
                )
                is AsyncImagePainter.State.Loading -> CircularProgressIndicator(modifier = Modifier.padding(46.dp))
                is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
            }
        }
    }
}

@Composable
fun crossFade(imageUrl: String) = ImageRequest.Builder(LocalContext.current)
    .addHeader("Authorization", Credentials.basic(MainActivity.UserName, MainActivity.UserPassword))
    .data(imageUrl)
    .crossfade(true)
    .build()

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
fun ErrorLayout() {
    Text(
        text = "No user identified :(",
        color = Color.White,
    )
}