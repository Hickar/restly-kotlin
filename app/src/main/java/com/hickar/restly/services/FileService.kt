package com.hickar.restly.services

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.hickar.restly.models.RequestFile

class FileService(
    private val contentResolver: ContentResolver
) {
    fun getRequestFile(uri: Uri): RequestFile? {
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                val size = if (!it.isNull(sizeIndex)) {
                    sizeIndex
                } else {
                    0
                }

                return RequestFile(name, uri.path.toString(), size)
            }
        }

        return null
    }
}