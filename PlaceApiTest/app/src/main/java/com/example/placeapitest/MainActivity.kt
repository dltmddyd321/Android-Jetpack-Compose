package com.example.placeapitest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.placeapitest.ui.theme.PlaceApiTestTheme
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

const val API_KEY = "AIzaSyDRLosw2hhvgfImxJ0m2cGly-lpZNlc14U"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaceApiTestTheme {
                PlaceSearchScreen()
            }
        }
    }
}

@Composable
fun rememberPlacesClient(context: Context): PlacesClient {
    return remember {
        Places.initializeWithNewPlacesApiEnabled(context, API_KEY)
        Places.createClient(context)
    }
}

@Composable
fun PlaceSearchScreen() {
    val context = LocalContext.current
    val placesClient = rememberPlacesClient(context)

    var query by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (it.length > 2) fetchAutoCompleteResults(it, placesClient) { results ->
                    predictions = results
                }
            },
            label = { Text("장소 검색") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "검색")
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(predictions) { prediction ->
                PlaceItem(prediction) { placeId ->
                    fetchPlaceDetails(placeId, placesClient) { place ->
                        Toast.makeText(context, "선택한 장소: ${place.name}", Toast.LENGTH_SHORT).show()
                        Log.e("Places API", "선택한 장소: ${place.nationalPhoneNumber} $place")
                    }
                }
            }
        }
    }
}

fun fetchAutoCompleteResults(
    query: String,
    placesClient: PlacesClient,
    onResult: (List<AutocompletePrediction>) -> Unit
) {
    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            onResult(response.autocompletePredictions)
        }
        .addOnFailureListener { exception ->
            Log.e("Places API", "AutoComplete Error: ${exception.message}")
        }
}

fun fetchPlaceDetails(
    placeId: String,
    placesClient: PlacesClient,
    onResult: (Place) -> Unit
) {
    val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)).build()

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            onResult(response.place)
        }
        .addOnFailureListener { exception ->
            Log.e("Places API", "Place Details Error: ${exception.message}")
        }
}

@Composable
fun PlaceItem(prediction: AutocompletePrediction, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(prediction.placeId) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = prediction.getPrimaryText(null).toString(), fontWeight = FontWeight.Bold)
            Text(text = prediction.getSecondaryText(null).toString(), color = Color.Gray)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlaceApiTestTheme {
        Greeting("Android")
    }
}