package com.example.figmasolution

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.figmasolution.actionbutton.ActionButton
import com.example.figmasolution.actionbutton.Design
import com.example.figmasolution.ui.theme.FigmaSolutionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FigmaSolutionTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    ActionButton(
                        text = "Hi, Youtube!",
                        modifier = Modifier.fillMaxWidth(),
                        design = Design.Primary,
                        onClick = {
                            Toast.makeText(
                                applicationContext,
                                "Oh, Don't touch me...", Toast.LENGTH_SHORT
                            ).show()
                        },
                        icon = {
                            Icon(
                                Icons.Default.Check,
                                null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ActionButton(
                        text = "Hi, WinD!",
                        modifier = Modifier.fillMaxWidth(),
                        design = Design.Primary,
                        onClick = {
                            Toast.makeText(
                                applicationContext,
                                "Thank you, man!", Toast.LENGTH_SHORT
                            ).show()
                        },
                        icon = {
                            Icon(
                                Icons.Default.Check,
                                null
                            )
                        }
                    )
                }
            }
        }
    }
}