package com.albertolicea00.myussdcodes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.albertolicea00.myussdcodes.ui.AppRoot
import com.albertolicea00.myussdcodes.ui.theme.MyUssdCodesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyUssdCodesTheme {
                AppRoot()
            }
        }
    }
}
