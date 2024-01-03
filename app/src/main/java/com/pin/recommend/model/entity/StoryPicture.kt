package com.pin.recommend.model.entity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.R
import com.pin.recommend.model.entity.Story
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Story::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("storyId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
class StoryPicture {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(index = true)
    var storyId: Long = 0
    var uri: String? = null

    fun testGetBitmap(context: Context?, width: Int, height: Int): Bitmap {
        var bitmap = BitmapUtility.decodeResource(context, R.drawable.test, width, height)
        bitmap = BitmapUtility.getRoundedCornerBitmap(bitmap, 50)
        return bitmap
    }

    fun getBackgroundDrawable(context: Context, w: Int, h: Int): Drawable {
        val bitmap = BitmapUtility.readPrivateImage(context, uri, w, h)
        return BitmapDrawable(context.resources, bitmap)
    }

    fun getBitmap(
        context: Context?,
        width: Int,
        height: Int
    ): Bitmap? {
        if(uri == null) return null
        return BitmapUtility.readPrivateImage(context, uri, width, height)
    }

    fun saveImage(context: Context?, bitmap: Bitmap?): Boolean {
        if (BitmapUtility.fileExistsByPrivate(context, uri)) {
            BitmapUtility.deletePrivateImage(context, uri)
        }
        val filename = BitmapUtility.generateFilename()
        val ext = ".png"
        val success = BitmapUtility.insertPrivateImage(context, bitmap, filename, ext)
        uri = filename + ext
        return success
    }

    fun deleteImage(context: Context?): Boolean {
        if (uri != null) {
            BitmapUtility.deletePrivateImage(context, uri)
            uri = null
            return true
        }
        return false
    }

    fun toDraft(context: Context): Draft {
        return Draft(getBitmap(context, 500, 500))
    }

    class Draft(
        var bitmap: Bitmap?
    ) {
        val uuid = UUID.randomUUID()
    }
}