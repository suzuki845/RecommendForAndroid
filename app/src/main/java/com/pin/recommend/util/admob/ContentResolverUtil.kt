package com.pin.recommend.util.admob

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream

class ContentResolverUtil {
    companion object {
        fun insertImage(
            context: Context,
            bitmap: Bitmap,
            format: Bitmap.CompressFormat,
            mimeType: String,  // "image/jpeg" など
            fileName: String,
        ): Uri {
            val collection = if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                // データ書き込みの場合は MediaStore.VOLUME_EXTERNAL_PRIMARY が適切
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } // collection = "content://media/external/images/media" のような Content URI
            // destination = "content://media/external/images/media/{id}" のような Content URI
            val destination = context.contentResolver.insert(collection,
                ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                        put(MediaStore.Images.Media.IS_PENDING, true)
                    }
                }
            ) ?: throw IllegalStateException("保存メディアファイルの作成に失敗しました")
            var output: OutputStream? = null
            try {
                output = context.contentResolver.openOutputStream(destination)
                    ?: error("保存メディアファイルを開けませんでした")
                bitmap.compress(format, 95, output)
            } catch (e: FileNotFoundException) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    context.contentResolver.delete(destination, null, null)
                }
                throw IllegalStateException(e)
            } catch (e: IOException) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    context.contentResolver.delete(destination, null, null)
                }
                throw e
            } finally {
                output?.close()
            }
            if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                context.contentResolver.update(destination, ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, false)
                }, null, null)
            }
            return destination
        }

    }
}