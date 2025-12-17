package af.mobile.healthycheck.ui.features.healthcheck

import af.mobile.healthycheck.ui.features.healthcheck.sections.AppetiteSection
import af.mobile.healthycheck.ui.features.healthcheck.sections.DigestionSection
import af.mobile.healthycheck.ui.features.healthcheck.sections.IdentitySection
import af.mobile.healthycheck.ui.features.healthcheck.sections.SymptomsSection
import af.mobile.healthycheck.ui.features.healthcheck.sections.VitalSection
import af.mobile.healthycheck.ui.features.healthcheck.sections.historySection
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckInput
import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckSummary
import af.mobile.healthycheck.ui.features.healthcheck.viewmodel.InputViewModel
import af.mobile.healthycheck.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(navController: NavHostController, vm: InputViewModel = viewModel()) {
    // --- STATE FORM INPUT ---
    var gender by remember { mutableStateOf("M") }
    var ageInput by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("") }
    var vomit by remember { mutableStateOf("") }
    var diapers by remember { mutableStateOf("") }
    var appetite by remember { mutableStateOf(3f) }
    var stoolFreq by remember { mutableStateOf("") }
    var stoolColor by remember { mutableStateOf("Coklat") }

    // State Gejala
    var symptomCough by remember { mutableStateOf(false) }
    var symptomRash by remember { mutableStateOf(false) }
    var symptomFlu by remember { mutableStateOf(false) }
    var symptomDifficultBreath by remember { mutableStateOf(false) }
    var symptomHardToNurse by remember { mutableStateOf(false) }

    // Validasi
    val isFormValid = ageInput.isNotBlank() && temp.isNotBlank() && vomit.isNotBlank() && diapers.isNotBlank() && stoolFreq.isNotBlank()

    // --- STATE VIEWMODEL ---
    val history by vm.history.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val isEndOfList by vm.isEndOfList.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()

    // --- LOGIC ---
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val resultLive = savedStateHandle?.getLiveData<HealthCheckSummary>("healthResult")
    val returnedSummary = resultLive?.observeAsState()?.value

    LaunchedEffect(returnedSummary) {
        returnedSummary?.let { summary ->
            vm.addHistory(summary)
            savedStateHandle.remove<HealthCheckSummary>("healthResult")
        }
    }

    LaunchedEffect(Unit) { vm.fetchHistory() }

    // --- UI ---
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BabyGrow", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                        Text("Monitor Kesehatan Anak", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Articles.route) }) {
                        Icon(Icons.Outlined.Lightbulb, contentDescription = "Tips", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // 1. INPUT SECTIONS
            item { IdentitySection(gender, { gender = it }, ageInput, { ageInput = it }) }
            item {
                VitalSection(
                    temp,
                    { temp = it },
                    vomit,
                    { vomit = it },
                    diapers,
                    { diapers = it })
            }
            item { AppetiteSection(appetite, { appetite = it }) }
            item {
                DigestionSection(
                    stoolFreq,
                    { stoolFreq = it },
                    stoolColor,
                    { stoolColor = it })
            }

            item {
                SymptomsSection(
                    symptomCough, { symptomCough = it },
                    symptomRash, { symptomRash = it },
                    symptomFlu, { symptomFlu = it },
                    symptomDifficultBreath, { symptomDifficultBreath = it },
                    symptomHardToNurse, { symptomHardToNurse = it }
                )
            }

            // 2. TOMBOL ANALISA
            item {
                if (isFormValid) {
                    Button(
                        onClick = {
                            val symptoms = mutableListOf<String>()
                            if (symptomCough) symptoms.add("Batuk")
                            if (symptomRash) symptoms.add("Ruam")
                            if (symptomFlu) symptoms.add("Flu")
                            if (symptomDifficultBreath) symptoms.add("Sesak Nafas")
                            if (symptomHardToNurse) symptoms.add("Sulit Menyusu")

                            val input = HealthCheckInput(
                                gender, ageInput.toIntOrNull() ?: 0, temp.toDoubleOrNull(),
                                vomit.toIntOrNull() ?: 0, diapers.toIntOrNull() ?: 0,
                                appetite.toInt(), stoolFreq.toIntOrNull() ?: 0, stoolColor, symptoms
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set("isHistoryView", false)
                            navController.currentBackStackEntry?.savedStateHandle?.set("healthInput", input)
                            navController.navigate(Screen.Result.route)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Analisa Kesehatan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                    }
                } else {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Analisa Kesehatan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                    }
                }
            }

            // 3. HISTORY SECTION
            historySection(
                history = history,
                isLoading = isLoading,
                isLoadingMore = isLoadingMore,
                isEndOfList = isEndOfList,
                onLoadMore = { vm.loadMoreHistory() },
                onShowLess = { vm.showLessHistory() },
                onItemClick = { item ->
                    if (item.inputData != null) {
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("healthInput", item.inputData)
                            set("isHistoryView", true)
                            set("fullSummary", item)
                        }
                        navController.navigate(Screen.Result.route)
                    }
                }
            )
        }
    }
}