package com.example.jsplutkalc

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jsplutkalc.models.Trip
import com.example.jsplutkalc.network.RetrofitInstance
import com.example.jsplutkalc.ui.theme.JSPLUTKALCTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.rememberNavController
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JSPLUTKALCTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TipperTracker(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun TipperTracker(modifier: Modifier = Modifier) {
    var vehicleId by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf<Long?>(null) }
    var stopTime by remember { mutableStateOf<Long?>(null) }
    val vehicleTypes = listOf(
        "Dumper",  "Excavator", "Loader", "Dozer", "Surface Miner", "Water Tanker","Wheel Loader","Surface Drilling","MobiScreen EVO","Vibratory Compactor"
,"Welding","Compressor","Ambulance","Pump (Diesel)","Pump (Diesel) \n" +
                "Washing","DG Tower","DG Main","Pump"    ).sorted()
    var expanded by remember { mutableStateOf(false) }
    var selectedVehicleType by remember { mutableStateOf(vehicleTypes[0]) }

    val context = LocalContext.current
    val logo: Painter = painterResource(id = R.mipmap.ic_launcher)
    Box(
        modifier= Modifier
            .fillMaxSize()


    ){

    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = logo,
            contentDescription = "Logo",
            modifier = Modifier.size(64.dp)
        )
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {


        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Vehicle Type: $selectedVehicleType",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(IntrinsicSize.Max)
                ) {
                    vehicleTypes.forEach { vehicleType ->
                        DropdownMenuItem(
                            onClick = {
                                selectedVehicleType = vehicleType
                                expanded = false
                            },
                            text = { Text(text = vehicleType) }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            TextField(
                value = vehicleId,
                onValueChange = { vehicleId = it },
                label = { Text("Enter Vehicle ID") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                startTime = System.currentTimeMillis()
                stopTime = null
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Time")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                stopTime = System.currentTimeMillis()
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Stop Time")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (startTime != null && stopTime != null) {
                    val trip = Trip(
                        vehicleType = selectedVehicleType,
                        vehicleId = vehicleId,
                        startTime = startTime.toString(),
                        endTime = stopTime.toString()
                    )
                    saveTrip(trip, context)
                    vehicleId = ""
                    startTime = null
                    stopTime = null
                    selectedVehicleType = vehicleTypes[0]

                } else {
                    Toast.makeText(context, "Please start and stop the timer first.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Trip")
        }
//        Button(onClick = {
//            val navController = rememberNavController()
//        }) {
//
//        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
        }
    }
}

private fun saveTrip(trip: Trip, context: Context) {

    RetrofitInstance.api.postTrip(trip).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Toast.makeText(context, "Trip saved successfully!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, "Failed to save trip", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

@Preview(showBackground = true)
@Composable
fun TipperTrackerPreview() {
    JSPLUTKALCTheme {
        TipperTracker(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        )
    }
}
