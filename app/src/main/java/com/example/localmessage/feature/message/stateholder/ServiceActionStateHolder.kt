package com.example.localmessage.feature.message.stateholder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.localmessage.feature.message.uistatemodel.ServiceActionUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.io.FileDescriptor

@Composable
fun rememberServiceActionState(
    scope: CoroutineScope,
    context: Context,
    activityResultRegistry: ActivityResultRegistry?,
    onRequestClick: (String) -> Unit,
): ServiceActionStateHolder {
    return remember(scope, context, activityResultRegistry) {
        ServiceActionStateHolder(
            scope = scope,
            context = context,
            activityResultRegistry = activityResultRegistry,
            onRequestClick = onRequestClick
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class ServiceActionStateHolder(
    private val scope: CoroutineScope,
    private val context: Context,
    private val activityResultRegistry: ActivityResultRegistry?,
    private val onRequestClick: (String) -> Unit
) {

    private val _uiState = MutableStateFlow<ServiceActionUIState?>(null)
    val uiState = _uiState.asStateFlow()

    fun setText(text: String) {
        _uiState.update {
            ServiceActionUIState(
                message = text,
                image = it?.image,
                isLoadingImage = false
            )
        }
    }

    fun testRequest() {
        onRequestClick(_uiState.value?.message?:"")
        _uiState.value = ServiceActionUIState(
            message = null,
            image = null,
            isLoadingImage = false
        )
    }

    fun chooseImage() {
        val pickMedia = activityResultRegistry?.register("", ActivityResultContracts.PickVisualMedia()) { uri ->
            _uiState.update {
                ServiceActionUIState(
                    message = it?.message,
                    image = null,
                    isLoadingImage = true
                )
            }
            scope.launch(newSingleThreadContext("compress_image")) {
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    val compressedImage = async { getBitmapFromUri(uri) }.await()
                    _uiState.update {
                        ServiceActionUIState(
                            message = it?.message,
                            image = compressedImage,
                            isLoadingImage = false
                        )
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                    _uiState.update {
                        ServiceActionUIState(
                            message = it?.message,
                            image = null,
                            isLoadingImage = false
                        )
                    }
                }
            }
        }

        pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
//        val outputStream = ByteArrayOutputStream()
//        image.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
//        val compressedImage = BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()))
        val reducedSizeImage = Bitmap.createScaledBitmap(
            image,
            (image.width*0.5f).toInt(),
            (image.height*0.5f).toInt(),
            true
        )
        parcelFileDescriptor?.close()
        Log.d("image", "byteCount : ${image.byteCount}, compressed byteCount : ${reducedSizeImage.byteCount}")
        return reducedSizeImage
    }

    fun clearChosenImage() {
        _uiState.update {
            ServiceActionUIState(
                message = it?.message,
                image = null,
                isLoadingImage = false
            )
        }
    }

}