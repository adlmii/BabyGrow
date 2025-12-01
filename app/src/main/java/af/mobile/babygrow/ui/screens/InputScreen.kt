package af.mobile.babygrow.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import af.mobile.babygrow.ui.components.*
import af.mobile.babygrow.ui.model.HealthCheckInput
import af.mobile.babygrow.ui.model.HealthCheckSummary
import af.mobile.babygrow.ui.viewmodel.InputViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(navController: NavHostController, vm: InputViewModel = viewModel()) {
    var gender by remember { mutableStateOf("M") }
    var temp by remember { mutableStateOf("") }
    var vomit by remember { mutableStateOf("") }
    var diapers by remember { mutableStateOf("") }
    var appetite by remember { mutableStateOf(3f) }
    var stoolFreq by remember { mutableStateOf("") }

    // UPDATE: Default value dalam Bahasa Indonesia
    var stoolColor by remember { mutableStateOf("Coklat") }

    var symptomCough by remember { mutableStateOf(false) }
    var symptomRash by remember { mutableStateOf(false) }
    var symptomFlu by remember { mutableStateOf(false) }
    var symptomDifficultBreath by remember { mutableStateOf(false) }
    var symptomHardToNurse by remember { mutableStateOf(false) }

    val isFormValid = temp.isNotBlank() && vomit.isNotBlank() && diapers.isNotBlank() && stoolFreq.isNotBlank()
    val history by vm.history.collectAsState()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val resultLive = savedStateHandle?.getLiveData<HealthCheckSummary>("healthResult")
    val returnedSummary = resultLive?.observeAsState()?.value

    LaunchedEffect(returnedSummary) {
        returnedSummary?.let { summary ->
            vm.addHistory(summary)
            savedStateHandle.remove<HealthCheckSummary>("healthResult")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "BabyGrow",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Monitor Kesehatan Anak",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                // [BAGIAN BARU] Tombol Navigasi ke Halaman Artikel (API)
                actions = {
                    IconButton(onClick = { navController.navigate("articles") }) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb, // Ikon Lampu
                            contentDescription = "Tips Edukasi",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // --- SECTION 1: Identitas (Gender) ---
            item {
                ModernCard(
                    title = "Jenis Kelamin Anak",
                    icon = Icons.Outlined.Person
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GenderButton(
                            text = "Laki-laki",
                            icon = Icons.Outlined.Male,
                            selected = gender == "M",
                            modifier = Modifier.weight(1f)
                        ) { gender = "M" }

                        GenderButton(
                            text = "Perempuan",
                            icon = Icons.Outlined.Female,
                            selected = gender == "F",
                            modifier = Modifier.weight(1f)
                        ) { gender = "F" }
                    }
                }
            }

            // --- SECTION 2: Tanda Vital ---
            item {
                ModernCard(
                    title = "Tanda Vital",
                    icon = Icons.Outlined.MonitorHeart
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ModernTextField(
                            value = temp,
                            onValueChange = { temp = it },
                            label = "Suhu Tubuh",
                            placeholder = "Contoh: 36.5",
                            suffix = "°C",
                            keyboardType = KeyboardType.Decimal,
                            leadingIcon = Icons.Outlined.Thermostat
                        )

                        ModernTextField(
                            value = vomit,
                            onValueChange = { vomit = it },
                            label = "Muntah",
                            suffix = "x / hari",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = Icons.Outlined.Sick
                        )

                        ModernTextField(
                            value = diapers,
                            onValueChange = { diapers = it },
                            label = "Popok Basah",
                            suffix = "x / hari",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = Icons.Outlined.WaterDrop
                        )
                    }
                }
            }

            // --- SECTION 3: Nafsu Makan ---
            item {
                ModernCard(
                    title = "Nafsu Makan",
                    icon = Icons.Outlined.Restaurant
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Tingkat Nafsu Makan",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "${appetite.toInt()}/5",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }

                        Slider(
                            value = appetite,
                            onValueChange = { appetite = it },
                            valueRange = 1f..5f,
                            steps = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            thumb = {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .shadow(4.dp, CircleShape)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        .border(4.dp, Color.White, CircleShape)
                                )
                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Tidak Mau",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                "Sangat Lahap",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // --- SECTION 4: Pencernaan / BAB ---
            item {
                ModernCard(
                    title = "Pencernaan (BAB)",
                    icon = Icons.Outlined.Spa
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModernTextField(
                            value = stoolFreq,
                            onValueChange = { stoolFreq = it },
                            label = "Frekuensi",
                            placeholder = "0",
                            suffix = "x / hari",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )

                        var expandedColor by remember { mutableStateOf(false) }

                        val colors = listOf("Coklat", "Kuning", "Hijau", "Putih Pucat", "Hitam", "Berdarah")

                        ExposedDropdownMenuBox(
                            expanded = expandedColor,
                            onExpandedChange = { expandedColor = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = stoolColor,
                                onValueChange = {},
                                label = { Text("Warna") },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedColor) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedColor,
                                onDismissRequest = { expandedColor = false }
                            ) {
                                colors.forEach { color ->
                                    DropdownMenuItem(
                                        text = { Text(color) },
                                        onClick = {
                                            stoolColor = color
                                            expandedColor = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- SECTION 5: Gejala Tambahan ---
            item {
                ModernCard(
                    title = "Gejala Tambahan",
                    icon = Icons.Outlined.WarningAmber
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                SymptomCheckbox("Batuk", symptomCough) { symptomCough = it }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                SymptomCheckbox("Ruam Kulit", symptomRash) { symptomRash = it }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                SymptomCheckbox("Flu / Pilek", symptomFlu) { symptomFlu = it }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                SymptomCheckbox("Sesak Napas", symptomDifficultBreath) { symptomDifficultBreath = it }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                SymptomCheckbox("Sulit Menyusu", symptomHardToNurse) { symptomHardToNurse = it }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // --- MAIN ACTION BUTTON ---
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
                        navController.currentBackStackEntry?.savedStateHandle?.set("isHistoryView", false)
                        navController.currentBackStackEntry?.savedStateHandle?.set("healthInput", input)
                        navController.navigate("result")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(if (isFormValid) 8.dp else 0.dp, RoundedCornerShape(16.dp)),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = if (!isFormValid) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Analisa Kesehatan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                }
            }

            // --- HISTORY LIST ---
            item {
                Text(
                    "Riwayat Pemeriksaan",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }

            if (history.isEmpty()) {
                item {
                    EmptyStateCard()
                }
            } else {
                items(history) { item ->
                    HistoryCard(item) {
                        if (item.inputData != null) {
                            navController.currentBackStackEntry?.savedStateHandle?.set("healthInput", item.inputData)
                            navController.currentBackStackEntry?.savedStateHandle?.set("isHistoryView", true)
                            navController.navigate("result")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SymptomCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .background(
                if (checked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
            color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                "Belum ada riwayat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Mulai pemeriksaan pertama untuk memantau kesehatan anak Anda.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}