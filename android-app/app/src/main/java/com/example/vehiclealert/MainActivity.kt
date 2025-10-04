package com.example.vehiclealert

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vehiclealert.ui.screens.CaptureScreen
import com.example.vehiclealert.ui.screens.RegistrationScreen
import com.example.vehiclealert.ui.theme.AppTheme
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PermissionsWrapper { AppNavHost() }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionsWrapper(content: @Composable () -> Unit) {
    val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.SEND_SMS,
        Manifest.permission.CALL_PHONE,
    )
    val state: MultiplePermissionsState = rememberMultiplePermissionsState(permissions)
    LaunchedEffect(Unit) {
        state.launchMultiplePermissionRequest()
    }
    content()
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            RegistrationScreen(onNavigateToCapture = { navController.navigate("capture") })
        }
        composable("capture") {
            CaptureScreen(onBack = { navController.popBackStack() })
        }
    }
}
