package com.pin.recommend.util

import android.Manifest
import android.os.Build

class PermissionRequests {

    fun requestImages(): List<PermissionRequest> {
        return if (Build.VERSION.SDK_INT > 32) {
            listOf(
                PermissionRequest(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    "画像ファイルへのアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください"
                )
            )
        } else {
            listOf(
                PermissionRequest(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    "外部ストレージへのアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください"
                ),
            )
        }
    }
}