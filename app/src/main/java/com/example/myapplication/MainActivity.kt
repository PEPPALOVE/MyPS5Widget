package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.ha.EntityState
import com.example.myapplication.ha.HomeAssistantClient
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var state by remember { mutableStateOf<EntityState?>(null) }
                    var error by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        try {
                            state = HomeAssistantClient.service.getEntityState(
                                HomeAssistantClient.TOKEN,
                                HomeAssistantClient.ENTITY_ID
                            )
                        } catch (e: Exception) {
                            error = e.message
                        }
                    }

                    Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                        Text(text = "Home Assistant Media Player", style = MaterialTheme.typography.headlineMedium)
                        
                        if (error != null) {
                            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
                        } else if (state == null) {
                            Text(text = "Loading...")
                        } else {
                            Greeting(name = state?.attributes?.friendlyName ?: "Player")
                            Text(text = "Status: ${state?.state}")
                            Text(text = "Now Playing: ${state?.attributes?.mediaTitle ?: "Nothing"}")
                        }
                        
                        Text(
                            text = "\nДобавьте виджет на главный экран, чтобы управлять плеером.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
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
    MyApplicationTheme {
        Greeting("Android")
    }
}