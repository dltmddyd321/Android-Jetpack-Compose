package com.windrr.couplewidgetapp.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.windrr.couplewidgetapp.activity.ui.theme.CoupleWidgetAppTheme
import com.windrr.couplewidgetapp.dday.dataStore
import com.windrr.couplewidgetapp.ui.theme.CreamWhite
import com.windrr.couplewidgetapp.ui.theme.SoftGray
import com.windrr.couplewidgetapp.ui.theme.SoftPeach
import com.windrr.couplewidgetapp.ui.theme.WarmText
import com.windrr.couplewidgetapp.widget.DDayGlanceWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val WIDGET_COLOR_KEY = intPreferencesKey("widget_color")
val BACKGROUND_IMAGE_URI_KEY = stringPreferencesKey("background_image_uri")
val LovelyPink = Color(0xFFFF8FAB)

class WidgetSettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            CoupleWidgetAppTheme {
                WidgetSettingScreen(onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun WidgetSettingScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val savedColorInt by context.dataStore.data
        .map { preferences ->
            preferences[WIDGET_COLOR_KEY] ?: android.graphics.Color.WHITE
        }
        .collectAsState(initial = android.graphics.Color.WHITE)
    val currentColor = Color(savedColorInt)

    val savedImageUriString by context.dataStore.data
        .map { preferences -> preferences[BACKGROUND_IMAGE_URI_KEY] ?: "" }
        .collectAsState(initial = "")

    var showColorPicker by remember { mutableStateOf(false) }

    // 사진 선택 런처 (Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedUri ->
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                context.contentResolver.takePersistableUriPermission(selectedUri, flag)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

            coroutineScope.launch {
                context.dataStore.edit { settings ->
                    settings[BACKGROUND_IMAGE_URI_KEY] = selectedUri.toString()
                }
                Toast.makeText(context, "배경 사진이 설정되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CreamWhite, SoftPeach.copy(alpha = 0.3f))
                )
            )
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = SoftGray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "설정",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = WarmText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showColorPicker = true }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "위젯 글자 색상",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = WarmText
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(currentColor)
                                .border(1.dp, Color.LightGray, CircleShape)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = SoftPeach.copy(alpha = 0.5f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "배경 사진 설정",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = WarmText
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F5F5))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (savedImageUriString.isNotEmpty()) {
                                UriImagePreview(uriString = savedImageUriString)
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = null,
                                    tint = SoftGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showColorPicker) {
        Dialog(onDismissRequest = { showColorPicker = false }) {
            key(showColorPicker) {
                val controller = rememberColorPickerController()
                var selectedColor by remember { mutableStateOf(currentColor) }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "색상 선택",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = WarmText
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HsvColorPicker(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            controller = controller,
                            initialColor = currentColor,
                            onColorChanged = { colorEnvelope: ColorEnvelope ->
                                selectedColor = colorEnvelope.color
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BrightnessSlider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(35.dp),
                            controller = controller,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("New:", color = SoftGray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(selectedColor)
                                        .border(1.dp, Color.LightGray, CircleShape)
                                )
                            }
                            Row {
                                TextButton(onClick = { showColorPicker = false }) {
                                    Text("취소", color = SoftGray)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            context.dataStore.edit { settings ->
                                                settings[WIDGET_COLOR_KEY] = selectedColor.toArgb()
                                            }
                                            DDayGlanceWidget.updateAllWidgets(context.applicationContext)
                                            Toast.makeText(
                                                context,
                                                "위젯 색상이 변경되었습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        showColorPicker = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = LovelyPink),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("저장", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UriImagePreview(uriString: String) {
    val context = LocalContext.current
    var bitmap by remember(uriString) {
        mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(
            null
        )
    }

    LaunchedEffect(uriString) {
        if (uriString.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                try {
                    val uri = uriString.toUri()
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val androidBitmap = BitmapFactory.decodeStream(inputStream)
                        bitmap = androidBitmap?.asImageBitmap()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = "Selected Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Icon(
            imageVector = Icons.Rounded.AccountCircle,
            contentDescription = null,
            tint = SoftGray,
            modifier = Modifier.size(20.dp)
        )
    }
}