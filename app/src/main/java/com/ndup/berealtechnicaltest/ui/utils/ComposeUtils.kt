package com.ndup.berealtechnicaltest.ui.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.onSimpleZoom(key1: Any? = Unit, action: (zoom: Float) -> Unit) = composed {
    pointerInput(key1) {
        detectTransformGestures { _, _, zoom, _ -> action(zoom) }
    }
}