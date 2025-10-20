package com.heklast.smartspender.feature.Profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePicker(onPicked: (String) -> Unit): () -> Unit {
    // TODO: implement an iOS picker (e.g., via UIDocumentPicker/Photos)
    return remember { {} }
}