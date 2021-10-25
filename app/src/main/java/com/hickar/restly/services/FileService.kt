package com.hickar.restly.services

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import com.hickar.restly.models.RequestFile
import java.io.*
import java.net.URI

class FileService(
    private val context: Context
) {
    private val contentResolver = context.contentResolver

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

                val extension = MimeTypeMap.getFileExtensionFromUrl(name)
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

                return RequestFile(name, uri.path.toString(), uri.toString(), mimeType!!, size)
            }
        }

        return null
    }

    fun createTempFile(inputStream: InputStream): File? {
        val cacheDir = context.externalCacheDir
        val tmpFile = File.createTempFile("response_body", "", cacheDir)

        try {
            val buffer = ByteArray(1024 * 8)
            val tmpFileOutputStream = FileOutputStream(tmpFile)

            while (inputStream.read() != -1) {
                tmpFileOutputStream.write(buffer, 0, inputStream.read())
            }

            tmpFileOutputStream.flush()
        } catch (exception: IOException) {
            Log.d("FileService", "Unable to write to tmp file: ${exception.message}")
            exception.printStackTrace()

            tmpFile.delete()
            return null
        }

        return tmpFile
    }

    fun readTempFile(uri: URI): InputStream {
        val file = File(uri)
        return FileInputStream(file)
    }

    fun deleteFile(file: File): Boolean {
        try {
            if (file.exists()) {
                file.delete()
            }
        } catch (exception: IOException) {
            Log.d("FileService", "File cannot be deleted: ${exception.message}")
            exception.printStackTrace()

            return false
        }

        return true
    }
}