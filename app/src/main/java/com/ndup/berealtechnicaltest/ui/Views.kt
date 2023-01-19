package com.ndup.berealtechnicaltest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.logging.ApiModelObject
import com.ndup.berealtechnicaltest.ui.utils.onSimpleZoom


@Composable
fun FullScreenImage(
    modifier: Modifier = Modifier,
    item: Item,
) {
    val imageUrl = "${ApiModelObject.baseUrl}/items/${item.id}/data"
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
    .addHeader(ApiModelObject.headerCredentialName, ApiModelObject.credential)
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

    // I wanted to deal a bit with remember pattern, I introduced the ability to zoom in & out the grid.
    var arrangementWidth by remember { mutableStateOf(initialItemWidthFactorised) }
    val arrangement = Arrangement.spacedBy(padding.dp)

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxHeight()
            .onSimpleZoom { zoom -> arrangementWidth = (arrangementWidth * zoom).coerceIn(60f..screenWidth) },
        verticalArrangement = arrangement,
        horizontalArrangement = arrangement,
        contentPadding = PaddingValues(padding.dp),
        columns = GridCells.Adaptive(arrangementWidth.dp),
    ) {
        items(items = list) {
            Item(
                item = it,
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

@Composable
@Preview
private fun PreviewGrid() {
    ItemGrid(
        list = Item.fromListJson("""[{"id":"d0626b4fc3cd2056341c385fc6f3025dab515ce3","parentId":"82a06b9e18ab2cba3c8edf379aa15eb482df0a1d",
            |"name":"P-Chan_by_el-maky-z.png","isDir":false,"size":491222,"contentType":"image/png","modificationDate":"2022-12-20T19:03:58.458752299Z"},{"id":"4ce6a56274e5733b34473a99d596cf7e0d26c6b5","parentId":"82a06b9e18ab2cba3c8edf379aa15eb482df0a1d","name":"gus2","isDir":true,"modificationDate":"2023-01-01T17:40:20.92298573Z"},{"id":"9e8bca2ce6bbb1a93abcf4b04119b739154f3a38","parentId":"82a06b9e18ab2cba3c8edf379aa15eb482df0a1d","name":"images.jpeg","isDir":false,"size":10712,"contentType":"image/jpeg","modificationDate":"2022-12-19T13:24:33.620810208Z"}]""".trimMargin()),
        modifier = Modifier.fillMaxHeight(),
        initialColumnCount = 1,
    )
}
@Composable
@Preview
private fun PreviewGrid3() {
    ItemGrid(
        list = Item.fromListJson("""[{"id":"d0626b4fc3cd2056341c385fc6f3025dab515ce3","parentId":"82a06b9e18ab2cba3c8edf379aa15eb482df0a1d",
            |"name":"P-Chan_by_el-maky-z.png","isDir":false,"size":491222,"contentType":"image/png","modificationDate":"2022-12-20T19:03:58.458752299Z"},{"id":"4ce6a56274e5733b34473a99d596cf7e0d26c6b5","parentId":"82a06b9e18ab2cba3c8edf379aa15eb482df0a1d","name":"gus2","isDir":true,"modificationDate":"2023-01-01T17:40:20.92298573Z"},{"id":"9e8bca2ce6bbb1a93abcf4b04119b739154f3a38","parentId":"82a06b9e18ab2cba3c8edf379aa15eb482df0a1d","name":"images.jpeg","isDir":false,"size":10712,"contentType":"image/jpeg","modificationDate":"2022-12-19T13:24:33.620810208Z"}]""".trimMargin()),
        modifier = Modifier.fillMaxHeight(),
        initialColumnCount = 3,
    )
}