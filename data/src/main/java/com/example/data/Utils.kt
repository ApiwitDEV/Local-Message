package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor

fun Uri.toBitmap(context: Context, sizeScale: Float): Bitmap {
    val parcelFileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(this, "r")
    val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
    val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
//        val outputStream = ByteArrayOutputStream()
//        image.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
//        val compressedImage = BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()))
    val reducedSizeImage = Bitmap.createScaledBitmap(
        image,
        (image.width*sizeScale).toInt(),
        (image.height*sizeScale).toInt(),
        true
    )
    parcelFileDescriptor?.close()
    Log.d("image", "byteCount : ${image.byteCount}, compressed byteCount : ${reducedSizeImage.byteCount}")
    return reducedSizeImage
}

fun ByteArray.byteArrayToBitmap(): Bitmap {
    val data = this
    return BitmapFactory.decodeByteArray(data, 0, data.size)
}

fun Bitmap.bitmapToByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 90, stream)
    return stream.toByteArray()
}