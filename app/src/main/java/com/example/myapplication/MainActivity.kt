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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
                    var isLoading by remember { mutableStateOf(false) }
                    val scope = rememberCoroutineScope()

                    fun loadData() {
                        scope.launch {
                            isLoading = true
                            error = null
                            try {
                                state = HomeAssistantClient.service.getEntityState(
                                    HomeAssistantClient.TOKEN,
                                    HomeAssistantClient.ENTITY_ID
                                )
                            } catch (e: Exception) {
                                error = e.toString()
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        loadData()
                    }

                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "Диагностика PS5", style = MaterialTheme.typography.headlineMedium)
                        
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }

                        if (error != null) {
                            Text(
                                text = "ОШИБКА:\n$error",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "\n1. Проверьте, что телефон в Wi-Fi 10.0.0.x\n2. Проверьте адрес http://10.0.0.44:8123 в браузере телефона.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else if (state != null) {
                            Text(text = "Связь установлена!", color = MaterialTheme.colorScheme.primary)
                            Greeting(name = state?.attributes?.friendlyName ?: "Player")
                            Text(text = "Статус: ${state?.state}")
                            Text(text = "Медиа: ${state?.attributes?.mediaTitle ?: "Ничего не играет"}")
                        }

                        Button(
                            onClick = { loadData() },
                            modifier = Modifier.padding(top = 16.dp),
                            enabled = !isLoading
                        ) {
                            Text("Проверить соединение")
                        }
                        
                        Text(
                            text = "\nIP: 10.0.0.44\nID: media_player.playstation_5",
                            style = MaterialTheme.typography.labelSmall
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