package com.pin.recommend.ui.passcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.SparseArray
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.pin.recommend.Constants
import com.pin.recommend.R
import com.pin.recommend.util.PrefUtil

class PassCodeConfirmationActivity : AppCompatActivity(), View.OnClickListener {
    private val TEXT_SUB_MISTAKE = "パスコードが間違っています。もう一度お試しください"
    private var text_sub_pass: TextView? = null
    private var array_image_view: Array<ImageView> = arrayOf()
    private val stringBuilder = StringBuilder()
    private val array_box = SparseArray<String>()
    private val bitmapBlack = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
    private val bitmapGrey = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
    private val pref by lazy { PrefUtil(this) }
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_pass_code)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        initViews()
        initCircleCanvas()
    }

    private fun initViews() {
        val actionBar = supportActionBar
        actionBar!!.title = "パスコード確認"

        text_sub_pass = findViewById<View>(R.id.text_sub_pass) as TextView
        array_image_view = arrayOf(
            findViewById<View>(R.id.circle1) as ImageView,
            findViewById<View>(R.id.circle2) as ImageView,
            findViewById<View>(R.id.circle3) as ImageView,
            findViewById<View>(R.id.circle4) as ImageView
        )

        val array_id = intArrayOf(
            R.id.box0,
            R.id.box1,
            R.id.box2,
            R.id.box3,
            R.id.box4,
            R.id.box5,
            R.id.box6,
            R.id.box7,
            R.id.box8,
            R.id.box9
        )
        for (i in 0..9) {
            findViewById<View>(array_id[i]).setOnClickListener(this)
            array_box.put(array_id[i], i.toString())
        }
    }

    private fun initCircleCanvas() {
        // black circle
        val canvas = Canvas(bitmapBlack)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawCircle(150f, 150f, 148f, paint)
        // grey circle
        val canvas2 = Canvas(bitmapGrey)
        val paint2 = Paint()
        paint2.color = Color.parseColor("#f5f5f5")
        paint2.style = Paint.Style.FILL
        paint2.isAntiAlias = true
        canvas2.drawCircle(150f, 150f, 148f, paint2)
    }

    private fun initStringBuilder() {
        stringBuilder.setLength(0)
        stringBuilder.trimToSize()
    }

    private fun initCircleColor() {
        for (circle in array_image_view) circle.setImageBitmap(bitmapGrey)
    }

    override fun onClick(v: View) {
        if (array_box.indexOfKey(v.id) >= 0) inputPassword(array_box[v.id])
    }

    fun onDelete(view: View?) {
        val length = stringBuilder.length
        deleteCircleColor(length)
        if (length != 0) stringBuilder.deleteCharAt(length - 1)
    }

    private fun deleteCircleColor(length: Int) {
        if (length > 0) array_image_view[length - 1].setImageBitmap(bitmapGrey)
    }

    private fun inputPassword(password: String) {
        val length = stringBuilder.length
        if (length > 3) return
        array_image_view[length].setImageBitmap(bitmapBlack)
        stringBuilder.append(password)
        if (length == 3) Handler().postDelayed({ confirmPassword() }, 200)
    }

    private fun confirmPassword() {
        if (stringBuilder.toString().toInt() == pref.getInt(Constants.PREF_KEY_PASSWORD)) {
            finish()
        } else {
            text_sub_pass!!.text = TEXT_SUB_MISTAKE
            initStringBuilder()
            initCircleColor()
        }
    }

    override fun onPause() {
        super.onPause()
        finish() //remove stack
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true) //Application will be background without regard for Activity stack
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        fun createIntent(context: Context?): Intent {
            return Intent(context, PassCodeConfirmationActivity::class.java)
        }
    }
}
