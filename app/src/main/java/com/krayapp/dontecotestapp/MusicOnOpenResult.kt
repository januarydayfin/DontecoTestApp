package com.krayapp.dontecotestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class MusicOnOpenResult:ActivityResultContract<String, Uri?>() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent()
            .setType("audio/mpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        when{
            resultCode != Activity.RESULT_OK -> null
            else -> intent?.data
        }
}