package com.albertolicea00.myussdcodes.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.albertolicea00.myussdcodes.data.model.UssdCode

object UssdDialer {

    /** Replaces every declared `{placeholder}` with the value entered by the user. */
    fun buildDialString(code: UssdCode, values: Map<String, String>): String =
        code.variables.fold(code.code) { acc, variable ->
            acc.replace("{${variable.key}}", values[variable.key].orEmpty().trim())
        }

    /**
     * Opens the system dialer with the code pre-filled (ACTION_DIAL needs no permission and
     * leaves the final "call" tap to the user — safest option for USSD).
     */
    fun dial(context: Context, dialString: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(dialString)}"))
        context.startActivity(intent)
    }
}
