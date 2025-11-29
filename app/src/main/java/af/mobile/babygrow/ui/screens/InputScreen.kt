package af.mobile.babygrow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
    var stoolColor by remember { mutableStateOf("Brown") }

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
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Cek Kesehatan Anak",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                ModernCard(
                    title = "Jenis Kelamin",
                    icon = Icons.Outlined.Face
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GenderButton(
                            text = "Laki-laki",
                            icon = "👦",
                            selected = gender == "M",
                            modifier = Modifier.weight(1f)
                        ) { gender = "M" }

                        GenderButton(
                            text = "Perempuan",
                            icon = "👧",
                            selected = gender == "F",
                            modifier = Modifier.weight(1f)
                        ) { gender = "F" }
                    }
                }
            }

            item {
                ModernCard(
                    title = "Data Kesehatan Dasar",
                    icon = Icons.Outlined.FavoriteBorder
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ModernTextField(
                            value = temp,
                            onValueChange = { temp = it },
                            label = "Suhu Tubuh",
                            placeholder = "37.5",
                            suffix = "°C",
                            keyboardType = KeyboardType.Decimal,
                            leadingIcon = Icons.Outlined.LocalFireDepartment
                        )

                        ModernTextField(
                            value = vomit,
                            onValueChange = { vomit = it },
                            label = "Frekuensi Muntah",
                            placeholder = "0",
                            suffix = "kali",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = Icons.Outlined.Warning
                        )

                        ModernTextField(
                            value = diapers,
                            onValueChange = { diapers = it },
                            label = "Popok Basah (24 jam)",
                            placeholder = "6",
                            suffix = "kali",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = Icons.Outlined.WaterDrop
                        )
                    }
                }
            }

            item {
                ModernCard(
                    title = "Nafsu Makan",
                    icon = Icons.Outlined.Restaurant
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Value indicator dengan background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Nafsu: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        "${appetite.toInt()}/5",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        // Slider dengan custom styling
                        Slider(
                            value = appetite,
                            onValueChange = { appetite = it },
                            valueRange = 1f..5f,
                            steps = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            thumb = {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(50),
                                            clip = false
                                        )
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(50)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                            },
                            track = { sliderPositions ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )

                        // Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "😢 Rendah",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "😋 Tinggi",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                ModernCard(
                    title = "Kondisi BAB",
                    icon = Icons.Outlined.LocalFireDepartment
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModernTextField(
                            value = stoolFreq,
                            onValueChange = { stoolFreq = it },
                            label = "Frekuensi",
                            placeholder = "3",
                            suffix = "kali",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )

                        var expandedColor by remember { mutableStateOf(false) }
                        val colors = listOf("Brown", "Yellow", "Green", "Pale White", "Black", "Bloody")

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
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
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

            item {
                ModernCard(
                    title = "Gejala Tambahan",
                    icon = Icons.Outlined.Warning
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SymptomCheckbox("Batuk", symptomCough) { symptomCough = it }
                        SymptomCheckbox("Ruam", symptomRash) { symptomRash = it }
                        SymptomCheckbox("Flu", symptomFlu) { symptomFlu = it }
                        SymptomCheckbox("Sesak Nafas", symptomDifficultBreath) { symptomDifficultBreath = it }
                        SymptomCheckbox("Sulit Menyusu", symptomHardToNurse) { symptomHardToNurse = it }
                    }
                }
            }

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
                        // Store input only temporarily for ResultScreen
                        navController.currentBackStackEntry?.savedStateHandle?.set("healthInput", input)
                        navController.navigate("result")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Mulai Pemeriksaan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Text(
                    "Riwayat Pemeriksaan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (history.isEmpty()) {
                item {
                    EmptyStateCard()
                }
            } else {
                items(history) { item ->
                    HistoryCard(item) {
                        val inputData = navController.currentBackStackEntry?.savedStateHandle
                            ?.get<HealthCheckInput>("healthInput_${item.timestamp}")

                        navController.currentBackStackEntry?.savedStateHandle?.set("detailItem", item)
                        if (inputData != null) {
                            navController.currentBackStackEntry?.savedStateHandle?.set("detailInput", inputData)
                        }
                        navController.navigate("detail")
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
                if (checked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
            color = if (checked) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
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
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("📋", style = MaterialTheme.typography.displayMedium)
            Text(
                "Belum ada riwayat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Mulai pemeriksaan pertama untuk melihat riwayat",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}