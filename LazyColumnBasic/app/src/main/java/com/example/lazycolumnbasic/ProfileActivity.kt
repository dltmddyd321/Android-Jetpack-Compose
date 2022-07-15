package com.example.lazycolumnbasic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.lazycolumnbasic.data.Puppy
import com.example.lazycolumnbasic.ui.theme.LazyColumnBasicTheme

class ProfileActivity : ComponentActivity() {

    private val puppy: Puppy by lazy {
        intent?.getSerializableExtra(PUPPY_ID) as Puppy
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumnBasicTheme {
                val context = LocalContext.current
                ProfileView(puppy = puppy) {
                    Toast.makeText(context, "Hello, ${puppy.title}!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val PUPPY_ID = "puppy_id"
        fun newIntent(context: Context, puppy: Puppy) =
            Intent(context, ProfileActivity::class.java).apply {
                putExtra(PUPPY_ID, puppy)
            }
    }
}

@Preview
@Composable
fun PreviewProfile() {
    val puppy = Puppy(
        id = 2,
        title = "Jubilee",
        sex = "Female",
        age = 6,
        description = "Jubilee enjoys thoughtful discussions by the campfire.",
        puppyImageId = R.drawable.p10
    )
    ProfileView(puppy) {}
}
