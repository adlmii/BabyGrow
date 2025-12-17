package af.mobile.healthycheck.ui.features.healthcheck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import af.mobile.healthycheck.ui.features.healthcheck.sections.*
import af.mobile.healthycheck.ui.components.SimpleHeader

@Composable
fun InputScreen(
    navController: NavHostController,
    vm: InputViewModel = viewModel()
) {
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

    // Logic Validasi
    val isFormValid = remember(ageInput, temp, vomit, diapers, stoolFreq) {
        ageInput.isNotBlank() && temp.isNotBlank() && vomit.isNotBlank() && diapers.isNotBlank() && stoolFreq.isNotBlank()
    }

    // --- STATE VIEWMODEL ---
    val history by vm.history.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val isEndOfList by vm.isEndOfList.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()

    // --- SIDE EFFECTS ---
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

    // --- MAIN LAYOUT ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Header
        SimpleHeader(
            title = "Cek Kesehatan",
            onBackClick = { navController.popBackStack() }
        )

        // 2. Content Form & History
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // --- SECTION 1: FORM INPUT ---
            item { IdentitySection(gender, { gender = it }, ageInput, { ageInput = it }) }

            item {
                VitalSection(
                    temp, { temp = it },
                    vomit, { vomit = it },
                    diapers, { diapers = it }
                )
            }

            item { AppetiteSection(appetite, { appetite = it }) }

            item {
                DigestionSection(
                    stoolFreq, { stoolFreq = it },
                    stoolColor, { stoolColor = it }
                )
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

            // --- SECTION 2: ACTION BUTTON ---
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (isFormValid) {
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

                            // Kirim Data & Navigasi
                            navController.currentBackStackEntry?.savedStateHandle?.set("isHistoryView", false)
                            navController.currentBackStackEntry?.savedStateHandle?.set("healthInput", input)
                            navController.navigate(Screen.Result.route)
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(if (isFormValid) 8.dp else 0.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Analisa Kesehatan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isFormValid) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                    }
                }
            }

            // --- SECTION 3: HISTORY ---
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