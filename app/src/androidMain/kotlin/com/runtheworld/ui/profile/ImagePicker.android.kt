package com.runtheworld.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberImagePickerLauncher(onBase64: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) { onBase64(null); return@rememberLauncherForActivityResult }
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: run { onBase64(null); return@rememberLauncherForActivityResult }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: run { onBase64(null); return@rememberLauncherForActivityResult }
        val side = minOf(bitmap.width, bitmap.height)
        val cropped = Bitmap.createBitmap(bitmap, (bitmap.width - side) / 2, (bitmap.height - side) / 2, side, side)
        val scaled = Bitmap.createScaledBitmap(cropped, 256, 256, true)
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 65, out)
        onBase64(Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP))
    }
    return { launcher.launch("image/*") }
}

@Composable
actual fun rememberBase64Bitmap(base64: String?): ImageBitmap? {
    return remember(base64) {
        if (base64 == null) return@remember null
        try {
            val bytes = Base64.decode(base64, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        } catch (_: Exception) { null }
    }
}
