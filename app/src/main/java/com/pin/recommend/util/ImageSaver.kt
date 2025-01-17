package com.pin.recommend.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ImageSaver(private val activity: Context) {
    private var saveDirName = DEFAULT_SAVE_DIRECTORY_NAME

    private var saveFileName: String? = DEFAULT_SAVE_FILE_NAME

    private var extension = DEFAULT_EXTENSION

    fun setSaveDirName(saveDirName: String) {
        this.saveDirName = saveDirName
    }

    fun setSaveFileName(saveFileName: String?) {
        this.saveFileName = saveFileName
    }

    fun setExtension(extension: String) {
        this.extension = extension
    }

    fun savePrivate(bitmap: Bitmap?): Boolean {
        var out: FileOutputStream? = null
        try {
            if (saveFileName == null) {
                return false
            }
            if (bitmap == null) {
                return false
            }
            val filename = saveFileName + extension
            out = activity.openFileOutput(filename, Context.MODE_PRIVATE)
            if (extension == ".png") {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            } else if (extension == ".jpg") {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            } else {
                return false
            }
        } catch (e: FileNotFoundException) {
            return false
        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                out = null
            }
        }
        return true
    }

    private val errors = ArrayList<String>()

    fun getErrors(): List<String> {
        return errors
    }

    fun errorCount(): Int {
        return errors.size
    }

    fun save(bitmap: Bitmap): Boolean {
        errors.clear()

        val pictureFolder = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )

        val dir = File(pictureFolder, saveDirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        if (!dir.exists()) {
            errors.add("dir make error")
            return false
        }

        val filename = saveFileName + extension
        val file = File(dir, filename)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file, false)
            if (extension == ".png") {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } else if (extension == ".jpg") {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            } else {
                errors.add("extension error")
                return false
            }

            fos.flush()

            val values = ContentValues()
            val contentResolver = activity.applicationContext.contentResolver
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Images.Media.SIZE, file.length())
            values.put(MediaStore.Images.Media.TITLE, file.name)
            values.put(MediaStore.Images.Media.DATA, file.path)
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (ie: IOException) {
                    fos = null
                }
            }
        }

        return true
    }


    companion object {
        private const val DEFAULT_SAVE_DIRECTORY_NAME = "ImageSaver"

        private const val DEFAULT_SAVE_FILE_NAME = "image_saver"

        private const val DEFAULT_EXTENSION = ".jpg"
    }
}
