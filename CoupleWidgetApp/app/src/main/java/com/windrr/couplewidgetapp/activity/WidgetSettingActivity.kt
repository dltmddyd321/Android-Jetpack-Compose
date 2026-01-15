package com.windrr.couplewidgetapp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.exifinterface.media.ExifInterface
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.windrr.couplewidgetapp.R
import com.windrr.couplewidgetapp.activity.ui.theme.CoupleWidgetAppTheme
import com.windrr.couplewidgetapp.dday.dataStore
import com.windrr.couplewidgetapp.ui.theme.CreamWhite
import com.windrr.couplewidgetapp.ui.theme.SoftGray
import com.windrr.couplewidgetapp.ui.theme.SoftPeach
import com.windrr.couplewidgetapp.ui.theme.WarmText
import com.windrr.couplewidgetapp.util.AppLanguageState
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
    val activity = context as? Activity
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
    var showImageOptionDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var isLanguageChanged by rememberSaveable { mutableStateOf(false) }

    fun handleBack() {
        if (isLanguageChanged) {
            activity?.setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("language_changed", true)
            })
        }
        onBackClick()
    }

    BackHandler {
        handleBack()
    }

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
                Toast.makeText(context, context.getString(R.string.msg_bg_set), Toast.LENGTH_SHORT)
                    .show()
                // 위젯 갱신
                DDayGlanceWidget.updateAllWidgets(context.applicationContext)
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
                IconButton(onClick = { handleBack() }) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.desc_back),
                        tint = SoftGray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_title),
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
                    // 위젯 글자 색상 옵션
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showColorPicker = true }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.widget_text_color),
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

                    // 배경 사진 설정 옵션
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // [수정] 바로 갤러리 안 가고 옵션 다이얼로그 띄움
                                showImageOptionDialog = true
                            }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.widget_bg_image_setting),
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

//                    HorizontalDivider(
//                        modifier = Modifier.padding(horizontal = 20.dp),
//                        color = SoftPeach.copy(alpha = 0.5f)
//                    )
//
//                    // 3. 언어 설정 옵션
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable { showLanguageDialog = true }
//                            .padding(horizontal = 20.dp, vertical = 16.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = stringResource(R.string.title_language_setting),
//                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
//                            color = WarmText
//                        )
//
//                        Image(
//                            painter = painterResource(id = R.drawable.lang),
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
//                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(SoftGray),
//                            modifier = Modifier.size(height = 28.dp, width = 80.dp)
//                        )
//                    }
                }
            }
        }
    }

    if (showLanguageDialog) {
        val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val languageState = remember { AppLanguageState() }

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = stringResource(R.string.title_language_setting),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WarmText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LanguageOptionItem(
                        text = stringResource(R.string.language_system),
                        selected = currentLocale.isEmpty(),
                        onClick = {
                            showLanguageDialog = false
                            languageState.updateLocale(context, "system")
                            (context as? Activity)?.recreate()
                            isLanguageChanged = true
                        }
                    )
                    LanguageOptionItem(
                        text = stringResource(R.string.language_ko),
                        selected = currentLocale.contains("ko"),
                        onClick = {
                            showLanguageDialog = false
                            languageState.updateLocale(context, "ko")
                            (context as? Activity)?.recreate()
                            isLanguageChanged = true
                        }
                    )
                    LanguageOptionItem(
                        text = stringResource(R.string.language_en),
                        selected = currentLocale.contains("en"),
                        onClick = {
                            showLanguageDialog = false
                            languageState.updateLocale(context, "en")
                            (context as? Activity)?.recreate()
                            isLanguageChanged = true
                        }
                    )
                    LanguageOptionItem(
                        text = stringResource(R.string.language_ja),
                        selected = currentLocale.contains("ja"),
                        onClick = {
                            showLanguageDialog = false
                            languageState.updateLocale(context, "ja")
                            (context as? Activity)?.recreate()
                            isLanguageChanged = true
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel), color = SoftGray)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showImageOptionDialog) {
        AlertDialog(
            onDismissRequest = { showImageOptionDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = stringResource(R.string.dialog_title_bg_option),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WarmText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageOptionDialog = false
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AccountBox, contentDescription = null, tint = LovelyPink)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.action_select_gallery),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageOptionDialog = false
                                coroutineScope.launch {
                                    context.dataStore.edit { settings ->
                                        settings[BACKGROUND_IMAGE_URI_KEY] = ""
                                    }
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.msg_bg_removed),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.action_remove_bg),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageOptionDialog = false }) {
                    Text(stringResource(R.string.cancel), color = SoftGray)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
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
                            text = stringResource(R.string.title_select_color),
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
                                Text(
                                    text = stringResource(R.string.label_color_new),
                                    color = SoftGray,
                                    fontSize = 12.sp
                                )
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
                                    Text(stringResource(R.string.cancel), color = SoftGray)
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
                                                context.getString(R.string.msg_color_changed),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        showColorPicker = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = LovelyPink),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.save),
                                        fontWeight = FontWeight.Bold
                                    )
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
        mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null)
    }

    LaunchedEffect(uriString) {
        if (uriString.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                try {
                    val uri = uriString.toUri()

                    var rotation = 0f
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val exif = ExifInterface(inputStream)
                        val orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                        rotation = when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                            else -> 0f
                        }
                    }

                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val originalBitmap = BitmapFactory.decodeStream(inputStream)
                        if (originalBitmap != null) {
                            bitmap = if (rotation != 0f) {
                                val matrix = Matrix().apply { postRotate(rotation) }
                                val rotatedBitmap = android.graphics.Bitmap.createBitmap(
                                    originalBitmap, 0, 0,
                                    originalBitmap.width, originalBitmap.height,
                                    matrix, true
                                )
                                if (rotatedBitmap != originalBitmap) {
                                    originalBitmap.recycle()
                                }
                                rotatedBitmap.asImageBitmap()
                            } else {
                                originalBitmap.asImageBitmap()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    bitmap = null
                }
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = stringResource(R.string.desc_selected_bg),
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

@Composable
fun LanguageOptionItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(if (selected) LovelyPink.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) LovelyPink else Color.Black,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = LovelyPink,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}