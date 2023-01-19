package com.ndup.berealtechnicaltest.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.ndup.berealtechnicaltest.R
import com.ndup.berealtechnicaltest.domain.Item


@OptIn(ExperimentalUnitApi::class)
@Composable
fun Item(
    item: Item,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.wrapContentWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = if (item.isDir) R.drawable.ic_folder else R.drawable.ic_image),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth(),
            text = item.name,
            fontSize = TextUnit(12f, TextUnitType.Sp),
            style = MaterialTheme.typography.titleMedium.copy(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            ),
            textAlign = TextAlign.Center,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

}


@Composable
@Preview
private fun PreviewItem() {
    Item(
        modifier = Modifier
            .background(color = Color.Black),
        item = Item.fromJson("{\"id\":\"da0e9a796b4c87d03663b7f05566c3fb87f24e80\",\"parentId\":\"3501582fd9cac5f197c1d3fc3e024a464b7db480\"," +
                "\"name\":\"image_493262.jpg\",\"isDir\":true,\"size\":2567402,\"contentType\":\"image/jpeg\"," +
                "\"modificationDate\":\"2023-01-18T21:36:40.676739918Z\"}")
    )
}