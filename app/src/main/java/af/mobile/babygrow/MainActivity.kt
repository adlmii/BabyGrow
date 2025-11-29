package af.mobile.babygrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import af.mobile.babygrow.ui.model.HealthCheckInput
import af.mobile.babygrow.ui.model.HealthCheckSummary
import af.mobile.babygrow.ui.viewmodel.InputViewModel
import af.mobile.babygrow.ui.viewmodel.ResultViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    Scaffold { inner ->
        Box(modifier = Modifier.padding(inner)) {
            AppNavHost(navController)
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "input") {
        composable("input") {
            val vm: InputViewModel = viewModel()
            InputScreen(navController = navController, vm = vm)
        }
        composable("result") {
            val vm: ResultViewModel = viewModel()
            ResultScreen(navController = navController, vm = vm)
        }
        dialog("detail") {
            DetailDialog(navController = navController)
        }
    }
}

@Composable
fun InputScreen(navController: NavHostController, vm: InputViewModel) {
    // form state
    var gender by remember { mutableStateOf("M") }
    var temp by remember { mutableStateOf("") }
    var vomit by remember { mutableStateOf("") }
    var diapers by remember { mutableStateOf("") }
    var appetite by remember { mutableStateOf(3f) }
    var stoolFreq by remember { mutableStateOf("") }
    var stoolColor by remember { mutableStateOf("Brown") }

    // Symptoms checkboxes
    var symptomCough by remember { mutableStateOf(false) }
    var symptomRash by remember { mutableStateOf(false) }
    var symptomFlu by remember { mutableStateOf(false) }
    var symptomDifficultBreath by remember { mutableStateOf(false) }
    var symptomHardToNurse by remember { mutableStateOf(false) }

    // Validation
    val isFormValid = temp.isNotBlank() && vomit.isNotBlank() && diapers.isNotBlank() && stoolFreq.isNotBlank()

    // Collect history state at the top level
    val history by vm.history.collectAsState()

    // observe returned summary from Result screen via SavedStateHandle
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val resultLive = savedStateHandle?.getLiveData<HealthCheckSummary>("healthResult")
    val returnedSummary = resultLive?.observeAsState()?.value

    // when a result arrives, add to vm.history
    LaunchedEffect(returnedSummary) {
        returnedSummary?.let { summary ->
            vm.addHistory(summary)
            savedStateHandle.remove<HealthCheckSummary>("healthResult")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Cek Kesehatan Anak",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        // Gender Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Jenis Kelamin",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = gender == "M", onClick = { gender = "M" })
                            Text("Laki-laki", modifier = Modifier.padding(start = 4.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = gender == "F", onClick = { gender = "F" })
                            Text("Perempuan", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }

        // Basic Health Inputs Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = temp,
                        onValueChange = { temp = it },
                        label = { Text("Suhu Tubuh (°C)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    OutlinedTextField(
                        value = vomit,
                        onValueChange = { vomit = it },
                        label = { Text("Frekuensi Muntah") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = diapers,
                        onValueChange = { diapers = it },
                        label = { Text("Popok Basah (24 jam)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        // Appetite Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Nafsu Makan",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Slider(
                        value = appetite,
                        onValueChange = { appetite = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Text(
                        "${appetite.toInt()}/5",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }

        // Stool Condition Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Kondisi BAB",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Frequency input field
                        OutlinedTextField(
                            value = stoolFreq,
                            onValueChange = { stoolFreq = it },
                            label = { Text("Frekuensi") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        // Color dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            var expanded by remember { mutableStateOf(false) }
                            val colors = listOf("Brown", "Yellow", "Green", "Pale White", "Black", "Bloody")

                            OutlinedTextField(
                                value = stoolColor,
                                onValueChange = { },
                                label = { Text("Warna") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                trailingIcon = { Text("▽") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            // Invisible clickable box overlay
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { expanded = true }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                colors.forEach { color ->
                                    DropdownMenuItem(
                                        text = { Text(color) },
                                        onClick = {
                                            stoolColor = color
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Additional Symptoms Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Gejala Tambahan",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = symptomCough, onCheckedChange = { symptomCough = it })
                                Text("Batuk", modifier = Modifier.padding(start = 4.dp))
                            }
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = symptomFlu, onCheckedChange = { symptomFlu = it })
                                Text("Flu", modifier = Modifier.padding(start = 4.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = symptomRash, onCheckedChange = { symptomRash = it })
                                Text("Ruam", modifier = Modifier.padding(start = 4.dp))
                            }
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = symptomDifficultBreath, onCheckedChange = { symptomDifficultBreath = it })
                                Text("Sesak Nafas", modifier = Modifier.padding(start = 4.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = symptomHardToNurse, onCheckedChange = { symptomHardToNurse = it })
                            Text("Sulit Menyusu", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    val symptoms = mutableListOf<String>()
                    if (symptomCough) symptoms.add("Batuk")
                    if (symptomRash) symptoms.add("Ruam")
                    if (symptomFlu) symptoms.add("Flu")
                    if (symptomDifficultBreath) symptoms.add("Sesak Nafas")
                    if (symptomHardToNurse) symptoms.add("Sulit Menyusu")

                    val input = HealthCheckInput(
                        gender = gender,
                        ageMonths = 12,
                        tempC = temp.toDoubleOrNull(),
                        vomitCount = vomit.toIntOrNull() ?: 0,
                        wetDiaperCount = diapers.toIntOrNull() ?: 0,
                        appetiteScore = appetite.toInt(),
                        stoolFreq = stoolFreq.toIntOrNull() ?: 0,
                        stoolColor = stoolColor,
                        symptoms = symptoms
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set("healthInput", input)
                    navController.navigate("result")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFF6200EE) else Color.Gray
                )
            ) {
                Text("Mulai Pemeriksaan", modifier = Modifier.padding(vertical = 4.dp))
            }
        }

        // History Section
        item {
            Text(
                "Riwayat",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (history.isEmpty()) {
            item {
                Text(
                    "Belum ada riwayat pemeriksaan",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(history) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Get the input data for this history item
                            val inputData = navController.currentBackStackEntry?.savedStateHandle
                                ?.get<HealthCheckInput>("healthInput_${item.timestamp}")

                            navController.currentBackStackEntry?.savedStateHandle?.set("detailItem", item)
                            if (inputData != null) {
                                navController.currentBackStackEntry?.savedStateHandle?.set("detailInput", inputData)
                            }
                            navController.navigate("detail")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(item.timestamp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            item.riskLevel,
                            fontWeight = FontWeight.SemiBold,
                            color = when(item.riskLevel.uppercase()) {
                                "HIGH" -> Color(0xFFD32F2F)
                                "MEDIUM" -> Color(0xFFF57C00)
                                else -> Color(0xFF388E3C)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavHostController, vm: ResultViewModel) {
    val input = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckInput>("healthInput")

    LaunchedEffect(input) {
        input?.let { vm.evaluate(it) }
    }

    val ui by vm.uiState.collectAsState()

    // Determine color based on risk level
    val riskColor = when(ui.riskLevel.uppercase()) {
        "HIGH" -> Color(0xFFD32F2F)      // Red
        "MEDIUM" -> Color(0xFFF57C00)    // Orange/Yellow
        else -> Color(0xFF388E3C)         // Green
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hasil Pemeriksaan") },
                navigationIcon = {
                    IconButton(onClick = {
                        val summary = HealthCheckSummary(
                            timestamp = System.currentTimeMillis(),
                            riskLevel = ui.riskLevel,
                            riskScore = ui.riskScore,
                            shortRecommendation = ui.recommendationShort
                        )
                        // always store summary
                        navController.previousBackStackEntry?.savedStateHandle?.set("healthResult", summary)
                        // also store the input paired with the same timestamp so detail can access it
                        input?.let { navController.previousBackStackEntry?.savedStateHandle?.set("healthInput_${summary.timestamp}", it) }
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Risk Level Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Kartu Risiko",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        ui.riskLevel,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = riskColor
                    )
                    Text(
                        "Skor: ${ui.riskScore}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Assessment Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Penilaian:",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ui.reasons.forEach { reason ->
                        Text(
                            "• $reason",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    // Show additional symptoms if any
                    if (input != null && input.symptoms.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Gejala Tambahan:",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        input.symptoms.forEach { symptom ->
                            Text(
                                "• $symptom",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Recommendation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Rekomendasi:",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        ui.recommendationShort,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun DetailDialog(navController: NavHostController) {
    val detail = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckSummary>("detailItem")
    val input = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckInput>("detailInput")

    if (detail != null) {
        AlertDialog(
            onDismissRequest = {
                navController.popBackStack()
            },
            containerColor = Color.White,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Detail Riwayat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("✕", style = MaterialTheme.typography.titleLarge)
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tanggal
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Tanggal",
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            )
                            Text(
                                SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(detail.timestamp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    item { Divider(color = Color.LightGray) }

                    // Risiko Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Risiko",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Risk", color = Color.DarkGray)
                                Text(
                                    detail.riskLevel,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when(detail.riskLevel.uppercase()) {
                                        "HIGH" -> Color(0xFFD32F2F)
                                        "MEDIUM" -> Color(0xFFF57C00)
                                        else -> Color(0xFF388E3C)
                                    }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Skor", color = Color.DarkGray)
                                Text(
                                    detail.riskScore.toString(),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Input Pemeriksaan Section
                    if (input != null) {
                        item { Divider(color = Color.LightGray) }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "Input Pemeriksaan",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                // Suhu
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Suhu", color = Color.DarkGray)
                                    Text(
                                        "${input.tempC ?: "-"} °C",
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Muntah
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Muntah", color = Color.DarkGray)
                                    Text("${input.vomitCount}x", fontWeight = FontWeight.Medium)
                                }

                                // Popok Basah
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Popok Basah", color = Color.DarkGray)
                                    Text("${input.wetDiaperCount}x", fontWeight = FontWeight.Medium)
                                }

                                // Nafsu Makan
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Nafsu Makan", color = Color.DarkGray)
                                    Text("${input.appetiteScore}/5", fontWeight = FontWeight.Medium)
                                }

                                // BAB
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("BAB", color = Color.DarkGray)
                                    Text(
                                        "${input.stoolFreq}x, ${input.stoolColor}",
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Gejala
                                if (input.symptoms.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("Gejala", color = Color.DarkGray)
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                input.symptoms.joinToString(", "),
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Divider(color = Color.LightGray) }

                    // Rekomendasi Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Rekomendasi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                detail.shortRecommendation,
                                color = Color.DarkGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = { }
        )
    } else {
        LaunchedEffect(Unit) { navController.popBackStack() }
    }
}