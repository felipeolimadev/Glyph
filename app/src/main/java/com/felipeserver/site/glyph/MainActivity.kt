package com.felipeserver.site.glyph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.felipeserver.site.glyph.navigation.AppNavigation
import com.felipeserver.site.glyph.ui.theme.GlyphTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlyphTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
