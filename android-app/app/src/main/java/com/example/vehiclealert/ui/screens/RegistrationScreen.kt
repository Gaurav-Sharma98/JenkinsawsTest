package com.example.vehiclealert.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.vehiclealert.data.AppDatabase
import com.example.vehiclealert.data.Owner
import com.example.vehiclealert.data.Vehicle
import com.example.vehiclealert.util.PlateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
fun RegistrationScreen(onNavigateToCapture: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Register Owner and Vehicle")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Owner name") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone number") }, keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Phone))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = whatsapp, onValueChange = { whatsapp = it }, label = { Text("WhatsApp number (optional)") }, keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Phone))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = plate, onValueChange = { plate = it }, label = { Text("Vehicle number (plate)") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(context = context)
                val ownerId = db.ownerDao().insert(Owner(name = name, phoneNumber = phone, whatsappNumber = whatsapp.ifBlank { null }))
                val normalized = PlateUtil.normalize(plate)
                db.vehicleDao().insert(Vehicle(plateNumber = normalized, ownerId = ownerId))
            }
        }) { Text("Save") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onNavigateToCapture) { Text("Go to Capture") }
    }
}
