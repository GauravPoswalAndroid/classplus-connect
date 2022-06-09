package com.fankonnect.app.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.text.ClipboardManager
import android.text.TextUtils
import android.widget.Toast
import com.fankonnect.app.R
import com.fankonnect.app.login.ui.WebViewActivity

object Utility {

    fun copyToClipboard(context: Context, text: String?) {
        if (TextUtils.isEmpty(text)) {
            return
        }
        try {
            val sdk = Build.VERSION.SDK_INT
            if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                val clipboard = context
                    .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = text
            } else {
                val clipboard = context
                    .getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = ClipData
                    .newPlainText(
                        context.getString(R.string.copied_text), text
                    )
                clipboard.setPrimaryClip(clip)
            }
            Toast.makeText(
                context,
                context.getString(R.string.copied_to_clipboard),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareMoreApps(context: Context, text: String?) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)))
    }

    fun shareTextOverApp(
        context: Context,
        packageName: String?,
        uriString: String?,
        message: String?
    ) {
        packageName ?: return

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        shareIntent.type = "text/plain"
        val packManager: PackageManager = context.packageManager
        val resolvedInfoList: List<ResolveInfo> =
            packManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
        var resolved = false
        for (resolveInfo in resolvedInfoList) {
            if (packageName == WebViewActivity.FACEBOOK_PACKAGE_NAME) {
                if (resolveInfo.activityInfo.packageName.startsWith(packageName)
                    || resolveInfo.activityInfo.packageName.toLowerCase()
                        .startsWith(WebViewActivity.FACEBOOK_LITE_PACKAGE_NAME)
                ) {
                    shareIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name
                    )
                    resolved = true
                }
            } else if (resolveInfo.activityInfo.packageName.startsWith(packageName)) {
                shareIntent.setClassName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )
                resolved = true
                break
            }
        }
        if (resolved) {
            context.startActivity(shareIntent)
        } else {
            val i = Intent()
            i.putExtra(Intent.EXTRA_TEXT, message)
            i.action = Intent.ACTION_VIEW
            uriString?.let {
                i.data = Uri.parse(it)
            }
            context.startActivity(i)
        }
    }
}