package com.avcialper.lemur.helper

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class UriToFile(private val context: Context) {

    fun convert(name: String, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile(name, ".jpg", context.cacheDir)
        file.deleteOnExit()
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

}