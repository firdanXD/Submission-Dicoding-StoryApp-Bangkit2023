package com.firdan.storyapp.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import com.firdan.storyapp.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
private const val MAX_IMAGE_SIZE = 1024 * 1024

val currentTimestamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createImageFile(application: Application): File {
    val mediaDirectory = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.getString(R.string.app_name)).apply {
            mkdir()
        }
    }

    val outputDirectory = mediaDirectory ?: application.filesDir

    return File(outputDirectory, "StoryApp$currentTimestamp.jpg")
}

fun createTempImageFile(context: Context): File {
    val storageDirectory: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("StoryApp$currentTimestamp", ".jpg", storageDirectory)
}

fun rotateBitmapWithOrientation(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    val rotationAngle = if (isBackCamera) 90f else -90f

    matrix.postRotate(rotationAngle)

    if (!isBackCamera) {
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    }

    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}

fun compressImageFile(file: File): File {
    val originalBitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val compressedStream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, compressedStream)
        val compressedByteArray = compressedStream.toByteArray()
        streamLength = compressedByteArray.size
        compressQuality -= 5
    } while (streamLength > MAX_IMAGE_SIZE)

    originalBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}
