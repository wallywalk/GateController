package com.cm.gatecontroller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
//import com.cm.uvsc.ui.theme.USCVTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Monitoring") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Gate row
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Gate", modifier = Modifier.weight(1f))
                Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("CLOSE") }
                    Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("OPEN") }
                }
            }

            // Two-button rows
            TwoButtonsRow("LAMP", "LED")
            TwoButtonsRow("RELAY1", "RELAY2")
            TwoButtonsRow("PHOTO1", "PHOTO2")
            TwoButtonsRow("OPEN1", "CLOSE1")
            TwoButtonsRow("OPEN2", "CLOSE2")
            TwoButtonsRow("OPEN3", "CLOSE3")
            TwoButtonsRow("LOOP A", "LOOP B")
            TwoButtonsRow("23.68V", "1234")

            // Delay row
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("DELAY", modifier = Modifier.weight(1f))
                Button(onClick = {}, modifier = Modifier.weight(2f)) { Text("30sec") }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TallButton("Test Start", modifier = Modifier.fillMaxWidth())
                TallButton("Config", modifier = Modifier.fillMaxWidth())
                TallButton("Board Test", modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Configuration") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Version
            LabelAndButtonRow("Version", "1.00JAN24")

            // Speeds
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LabelAndButtonRow("Open speed", "1", modifier = Modifier.weight(1f))
                LabelAndButtonRow("Close speed", "2", modifier = Modifier.weight(1f))
            }

            // Toggles
            TwoTogglesRow("LAMP", "BUZZER")
            TwoTogglesRow("LAMP ON", "LAMP OFF")

            // LED OPEN
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("LED OPEN", modifier = Modifier.weight(1f))
                Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("BLUE") }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Switch(checked = true, onCheckedChange = {})
                }
            }

            // LED CLOSE
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("LED CLOSE", modifier = Modifier.weight(1f))
                Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("RED") }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Switch(checked = false, onCheckedChange = {})
                }
            }

            // LOOP A/B
            TwoButtonsRow("LOOP A", "LOOP B")

            // DELAY TIME
            LabelAndButtonRow("DELAY TIME", "30sec")

            // RELAY
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LabelAndButtonRow("RELAY1", "5", modifier = Modifier.weight(1f))
                LabelAndButtonRow("RELAY2", "10", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TallButton("Relay Mode", modifier = Modifier.fillMaxWidth())
                TallButton("Load config", modifier = Modifier.fillMaxWidth())
                TallButton("Save config", modifier = Modifier.fillMaxWidth())
                TallButton("Factory", modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardTestScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Board Test") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Board Test Screen Content", style = MaterialTheme.typography.headlineSmall)
        }
    }
}


@Composable
fun TwoButtonsRow(text1: String, text2: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text1) }
        Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text2) }
    }
}

@Composable
fun TallButton(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = modifier.height(64.dp)
    ) {
        Text(text)
    }
}

@Composable
fun LabelAndButtonRow(label: String, buttonText: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))
        Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(buttonText) }
    }
}

@Composable
fun TwoTogglesRow(label1: String, label2: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(label1, modifier = Modifier.weight(1f))
            Switch(checked = true, onCheckedChange = {}, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(label2, modifier = Modifier.weight(1f))
            Switch(checked = false, onCheckedChange = {}, modifier = Modifier.weight(1f))
        }
    }
}


//@Preview(showBackground = true, widthDp = 400)
//@Composable
//fun MonitoringScreenPreview() {
////    USCVTheme {
//        MonitoringScreen()
////    }
//}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun ConfigurationScreenPreview() {
//    USCVTheme {
        ConfigurationScreen()
//    }
}

//@Preview(showBackground = true, widthDp = 400)
//@Composable
//fun BoardTestScreenPreview() {
//    USCVTheme {
//        BoardTestScreen()
//    }
//}