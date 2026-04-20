package com.runtheworld.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun rememberImagePickerLauncher(onBase64: (String?) -> Unit): () -> Unit

@Composable
expect fun rememberBase64Bitmap(base64: String?): ImageBitmap?
