package com.myciclo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.myciclo.app.navegacion.AppNavegacion
import com.myciclo.app.ui.theme.MyCicloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCicloTheme {
                AppNavegacion()
            }
        }
    }
}
