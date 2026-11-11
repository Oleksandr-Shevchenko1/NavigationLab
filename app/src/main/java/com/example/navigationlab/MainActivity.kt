package com.example.navigationlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.navigationlab.ui.theme.NavigationLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val model = ViewModelProvider(this)[LabViewModel::class.java]

        setContent {
            NavigationLabTheme {
                NavigationLabApp(model)
            }
        }
    }
}

enum class AppDestinations(val label: String, val symbol: String) {
    COUNTER("Лічильник", "+"),
    MESSAGE("Повідомлення", "✉"),
    SCORE("Бали", "★"),
}

class LabViewModel : ViewModel() {
    var score by mutableIntStateOf(0)
        private set

    fun addPoints() {
        score += 5
    }
}

@Composable
private fun NavigationLabApp(model: LabViewModel) {
    var destination by rememberSaveable { mutableStateOf(AppDestinations.COUNTER) }
    var count by remember { mutableIntStateOf(0) }
    var messageChanged by rememberSaveable { mutableStateOf(false) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { itemDestination ->
                item(
                    icon = { Text(itemDestination.symbol, fontSize = 22.sp) },
                    label = { Text(itemDestination.label) },
                    selected = destination == itemDestination,
                    onClick = { destination = itemDestination },
                )
            }
        },
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val screenModifier = Modifier.padding(innerPadding)

            when (destination) {
                AppDestinations.COUNTER ->
                    CounterScreen(count, { count++ }, screenModifier)

                AppDestinations.MESSAGE ->
                    MessageScreen(messageChanged, { messageChanged = !messageChanged }, screenModifier)

                AppDestinations.SCORE ->
                    ScoreScreen(model.score, model::addPoints, screenModifier)
            }
        }
    }
}

@Composable
private fun CounterScreen(count: Int, onAdd: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Натискань: $count", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = onAdd, modifier = Modifier.widthIn(min = 200.dp)) {
            Text("Додати натискання")
        }
    }
}

@Composable
private fun MessageScreen(changed: Boolean, onChange: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = if (changed) "Гарного дня!" else "Привіт!",
            style = MaterialTheme.typography.titleLarge,
        )
        OutlinedButton(onClick = onChange, modifier = Modifier.fillMaxWidth()) {
            Text("Змінити повідомлення")
        }
    }
}

@Composable
private fun ScoreScreen(score: Int, onAdd: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            text = "Набрано балів: $score",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Button(onClick = onAdd, modifier = Modifier.widthIn(min = 144.dp)) {
            Text("Додати 5 балів")
        }
    }
}
