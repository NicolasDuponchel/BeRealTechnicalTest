package com.ndup.berealtechnicaltest.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.ndup.berealtechnicaltest.login.ApiModelObject

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.onSimpleZoom(key1: Any? = Unit, action: (zoom: Float) -> Unit) = composed {
    pointerInput(key1) {
        detectTransformGestures { _, _, zoom, _ -> action(zoom) }
    }
}


@Composable
fun imageLoaderModel(imageUrl: String) = ImageRequest.Builder(LocalContext.current)
    .addHeader(ApiModelObject.headerCredentialName, ApiModelObject.credential)
    .data(imageUrl)
    .crossfade(true)
    .build()