package com.pin.recommend.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.UUID

object BitmapUtility {
    fun createBitmapByView(v: View): Bitmap {
        if (v.measuredHeight <= 0) {
            v.measure(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        v.draw(c)

        return b
    }

    private fun calculateSize(options: BitmapFactory.Options?, reqWidth: Int, reqHeight: Int) {
        val height = options!!.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            inSampleSize = if (width > height) {
                Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
    }

    fun newScaledBitmap(bitmap: Bitmap, reqWidth: Float, reqHeight: Float): Bitmap {
        val actWidth = bitmap.width.toFloat()
        val actHeight = bitmap.height.toFloat()
        val ratio = actWidth / actHeight
        var newWidth = reqWidth.toInt()
        var newHeight = reqHeight.toInt()
        if (reqWidth / reqHeight > ratio) {
            newWidth = (reqHeight * ratio).toInt()
        } else {
            newHeight = (reqWidth / ratio).toInt()
        }

        val scaled = Bitmap.createScaledBitmap(
            bitmap,
            newWidth, newHeight, false
        )
        return scaled
    }

    fun readPrivateImage(context: Context, filename: String?): Bitmap? {
        var input: InputStream? = null
        try {
            input = context.openFileInput(filename)
        } catch (e: FileNotFoundException) {
            return null
        }

        val bitmap = BitmapFactory.decodeStream(input)

        return bitmap
    }

    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    fun decodeUri(context: Context, uri: Uri?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: IOException) {
            return null
        }
        return bitmap
    }

    fun decodeUri(context: Context, uri: Uri?, width: Int, height: Int): Bitmap? {
        var input: InputStream? = null
        try {
            input = context.contentResolver.openInputStream(uri!!)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val option = getBitmapOptions(context, uri)

        if (option.outWidth < width || option.outHeight < height) {
            option.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeStream(input, null, option)
            try {
                input!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return bitmap
        }

        calculateSize(option, width, height)

        val bitmap = BitmapFactory.decodeStream(input, null, option)

        try {
            input!!.close()
        } catch (e: IOException) {
            return bitmap
        }

        return bitmap
    }

    fun decodeURL(context: Context?, urlString: String?): Bitmap? {
        var istream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            val url = URL(urlString)
            istream = url.openStream()
            bitmap = BitmapFactory.decodeStream(istream)
            istream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun getBitmapOptions(context: Context, imageUri: Uri?): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        try {
            val iStream = context.contentResolver.openInputStream(
                imageUri!!
            )
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(iStream, null, options)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return options
    }

    fun decodeResource(context: Context, resourceId: Int, width: Int, height: Int): Bitmap {
        val option = BitmapFactory.Options()
        val r = context.resources

        option.inJustDecodeBounds = true
        BitmapFactory.decodeResource(r, resourceId, option)

        if (option.outWidth < width || option.outHeight < height) {
            // 縦、横のどちらかが指定値より小さい場合は普通にBitmap生成
            option.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(r, resourceId, option)
        }

        calculateSize(option, width, height)

        val bitmap = BitmapFactory.decodeResource(r, resourceId, option)

        return bitmap
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getBitmap(context: Context, uri: String?, width: Int, height: Int): Bitmap? {
        val option = BitmapFactory.Options()
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                context.contentResolver.takePersistableUriPermission(
                    Uri.parse(uri),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            parcelFileDescriptor = context.contentResolver.openFileDescriptor(Uri.parse(uri), "r")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        option.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, option)

        if (option.outWidth < width || option.outHeight < height) {
            // 縦、横のどちらかが指定値より小さい場合は普通にBitmap生成
            option.inJustDecodeBounds = false
            return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, option)
        }

        calculateSize(option, width, height)

        val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, option)
        try {
            parcelFileDescriptor.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    fun resizeTo(image: Bitmap, width: Int, height: Int): Bitmap {
        var image = image
        val newImage = Bitmap.createScaledBitmap(
            image,
            width, height, true
        )
        image.recycle()
        image = newImage

        return image
    }


    fun thenBy(then: Double, actualPrimary: Double, actualSecondary: Double): Int {
        val ratio = actualSecondary / actualPrimary

        val maxSecondary = (Math.round(then * ratio)).toInt()
        return maxSecondary
    }

    fun getBitmapOptions(context: Context, filename: String?): BitmapFactory.Options? {
        var input: InputStream? = null
        try {
            input = context.openFileInput(filename)
        } catch (e: FileNotFoundException) {
            return null
        }

        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeStream(input, null, option)

        try {
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return option
        }

        return option
    }

    fun readPrivateImage(context: Context, filename: String?, width: Int, height: Int): Bitmap? {
        if (!fileExistsByPrivate(context, filename)) {
            return null
        }

        val option = getBitmapOptions(context, filename)

        var input: InputStream? = null
        try {
            input = context.openFileInput(filename)
        } catch (e: FileNotFoundException) {
            return null
        }

        if (option!!.outWidth < width || option.outHeight < height) {
            option.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeStream(input, null, option)
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return bitmap
        }

        calculateSize(option, width, height)

        val bitmap = BitmapFactory.decodeStream(input, null, option)

        try {
            input.close()
        } catch (e: IOException) {
            return bitmap
        }

        return bitmap
    }

    fun deletePrivateImage(context: Context, filename: String?) {
        context.deleteFile(filename)
    }

    fun insertPrivateImage(
        context: Context,
        bitmap: Bitmap?,
        filename: String?,
        extension: String
    ): Boolean {
        val imageSaver = ImageSaver(context)
        imageSaver.setSaveFileName(filename)
        imageSaver.setExtension(extension)

        return imageSaver.savePrivate(bitmap)
    }

    fun fileExistsByPrivate(context: Context, filename: String?): Boolean {
        if (filename == null) {
            return false
        }
        try {
            val file = context.getFileStreamPath(filename)
            if (file == null || !file.exists()) {
                return false
            }
        } catch (e: IllegalArgumentException) {
            return false
        }
        return true
    }

    fun generateFilename(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }
}