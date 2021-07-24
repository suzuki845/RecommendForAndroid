package com.pin.recommend.model.viewmodel

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import com.pin.recommend.MyApplication
import com.pin.recommend.R
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter
import java.lang.RuntimeException


object DataBindingAdapters {
    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageUri(view: ImageView, imageUri: String?) {
        if (imageUri == null) {
            view.setImageURI(null)
        } else {
            view.setImageURI(Uri.parse(imageUri))
        }
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageUri(view: ImageView, imageUri: Uri?) {
        view.setImageURI(imageUri)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageDrawable(view: ImageView, drawable: Drawable?) {
        view.setImageDrawable(drawable)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageBitmap(view: ImageView, bitmap: Bitmap?) {
        view.setImageBitmap(bitmap)
    }

    @JvmStatic
    @BindingAdapter("android:accountToolbar", "android:characterToolbar", "android:activity")
    fun setToolbar(toolbar: Toolbar, account: Account?, character: RecommendCharacter?, activity: Activity){
        if(character != null){
            val accountToolbarTextColor = account?.getToolbarTextColor() ?: Color.parseColor("#ffffff")
            val accountToolbarBackgroundColor =  account?.getToolbarBackgroundColor() ?: Color.parseColor("#eb34ab")
            toolbar.setBackgroundColor(character.getToolbarBackgroundColor(toolbar.context, accountToolbarBackgroundColor))
            toolbar.setTitleTextColor(character.getToolbarTextColor(toolbar.context, accountToolbarTextColor))
            val drawable = toolbar.overflowIcon?.let { DrawableCompat.wrap(it) }
            if (drawable != null) {
                DrawableCompat.setTint(drawable, character.getToolbarTextColor(toolbar.context, accountToolbarTextColor))
            }
            MyApplication.setupStatusBarColor(activity,
                    character.getToolbarTextColor(toolbar.context, accountToolbarTextColor),
                    character.getToolbarBackgroundColor(toolbar.context, accountToolbarBackgroundColor))
        }
    }

    @JvmStatic
    @BindingAdapter("android:homeText")
    fun setHomeText(textView: TextView, character: RecommendCharacter?){
        character?.getHomeTextColor()?.let { textView.setTextColor(it) }
        character?.getHomeTextShadowColor()?.let{ textView.setShadowLayer(4f, 0f, 0f, it) }
        try {
            if (character?.fontFamily != null && character.fontFamily != "default") {
                val font = Typeface.createFromAsset(textView.context.assets, "fonts/" + character.fontFamily + ".ttf")
                textView.typeface = font
            } else {
                textView.typeface = null
            }
        } catch (e: RuntimeException) {
            println("font missing " + character?.fontFamily)
        }
    }

    @JvmStatic
    @BindingAdapter("android:characterIcon")
    fun setCharacterIcon(imageView: ImageView, character: RecommendCharacter?) {
        imageView.setImageResource(R.drawable.ic_person_300dp)
        character?.getIconImage(imageView.context, 500, 500)?.let {
            imageView.setImageBitmap(it)
        }
    }

    @JvmStatic
    @BindingAdapter("android:characterBackground")
    fun setCharacterBackground(imageView: ImageView, character: RecommendCharacter?) {
        character?.getBackgroundDrawable(imageView.context, 1000, 1000)?.let {
            imageView.setImageDrawable(it)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        character?.backgroundImageOpacity?.let {
            imageView.alpha = character.backgroundImageOpacity
        }
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }
}