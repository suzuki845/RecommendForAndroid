package com.pin.recommend.util

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.pin.util.R

object RuntimePermissionUtil {
    @JvmStatic
    fun hasSelfPermissions(context: Context, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        for (permission in permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun checkGrantResults(vararg grantResults: Int): Boolean {
        require(grantResults.size != 0) { "grantResults is empty" }
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.shouldShowRequestPermissionRationale(permission)
        } else true
    }

    // ダイアログ表示
    @JvmStatic
    fun showAlertDialog(fragmentManager: FragmentManager, permission: String) {
        val dialog = RuntimePermissionAlertDialogFragment.newInstance(permission)
        dialog.show(fragmentManager, RuntimePermissionAlertDialogFragment.TAG)
    }

    // ダイアログ本体
    class RuntimePermissionAlertDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val permission = arguments?.getString(ARG_PERMISSION_NAME)
            val dialogBuilder = AlertDialog.Builder(activity)
                .setMessage(permission)
                .setPositiveButton(R.string.permission_app_info) { dialog, which ->
                    dismiss()
                    // システムのアプリ設定画面
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + activity?.packageName)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity?.startActivity(intent)
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> dismiss() }
            return dialogBuilder.create()
        }

        companion object {
            const val TAG = "RuntimePermissionApplicationSettingsDialogFragment"
            private const val ARG_PERMISSION_NAME = "permissionName"
            fun newInstance(permission: String): RuntimePermissionAlertDialogFragment {
                val fragment = RuntimePermissionAlertDialogFragment()
                val args = Bundle()
                args.putString(ARG_PERMISSION_NAME, permission)
                fragment.arguments = args
                return fragment
            }
        }
    }
}

object PermissionChecker {

    @JvmStatic
    fun hasSelfPermissions(
        context: Context,
        permissions: List<PermissionRequest>
    ): List<PermissionDenied> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return listOf()
        }
        val has = mutableListOf<PermissionDenied>()
        for (permission in permissions) {
            if (context.checkSelfPermission(permission.name) != PackageManager.PERMISSION_GRANTED) {
                has.add(PermissionDenied(permission))
            }
        }
        return has
    }

    @JvmStatic
    fun shouldShowRequestPermissionRationale(
        activity: Activity,
        permissionDeniedList: List<PermissionDenied>
    ): List<PermissionDenied> {
        val result = mutableListOf<PermissionDenied>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissionDeniedList) {
                if (activity.shouldShowRequestPermissionRationale(permission.name)) {
                    result.add(permission)
                }
            }
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @JvmStatic
    fun requestPermissions(
        activity: AppCompatActivity,
        requestCode: Int,
        permissions: List<PermissionRequest>
    ): Boolean {
        val deniedList = hasSelfPermissions(
            activity, permissions
        )

        if (deniedList.isNotEmpty()) {
            val requestDialogs =
                shouldShowRequestPermissionRationale(activity, deniedList)

            for (requestDialog in requestDialogs) {
                showPermissionRationaleDialog(
                    activity.supportFragmentManager,
                    requestDialog.rationaleMessage
                )
            }

            val requests = deniedList.filter { deny ->
                requestDialogs.firstOrNull { it.name == deny.name } == null
            }

            if (requests.isNotEmpty()) {
                activity.requestPermissions(
                    requests.map { it.name }.toTypedArray(),
                    requestCode
                )
            }
            return false
        }

        return true
    }

    @JvmStatic
    fun showPermissionRationaleDialog(fragmentManager: FragmentManager, permission: String) {
        val dialog = RuntimePermissionAlertDialogFragment.newInstance(permission)
        dialog.show(fragmentManager, RuntimePermissionAlertDialogFragment.TAG)
    }

    // ダイアログ本体
    class RuntimePermissionAlertDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val permission = arguments?.getString(ARG_PERMISSION_NAME)
            val dialogBuilder = AlertDialog.Builder(activity)
                .setMessage(permission)
                .setPositiveButton(R.string.permission_app_info) { dialog, which ->
                    dismiss()
                    // システムのアプリ設定画面
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + activity?.packageName)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity?.startActivity(intent)
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> dismiss() }
            return dialogBuilder.create()
        }

        companion object {
            const val TAG = "RuntimePermissionApplicationSettingsDialogFragment"
            private const val ARG_PERMISSION_NAME = "permissionName"
            fun newInstance(permission: String): RuntimePermissionAlertDialogFragment {
                val fragment = RuntimePermissionAlertDialogFragment()
                val args = Bundle()
                args.putString(ARG_PERMISSION_NAME, permission)
                fragment.arguments = args
                return fragment
            }
        }
    }

}

class PermissionRequest(val name: String, val rationaleMessage: String)

class PermissionDenied(val request: PermissionRequest) {
    val name = request.name
    val rationaleMessage = request.rationaleMessage
}

