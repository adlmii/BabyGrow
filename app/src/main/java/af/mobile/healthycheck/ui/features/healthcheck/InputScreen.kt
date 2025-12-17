package af.mobile.healthycheck.ui.features.healthcheck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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
    // --- 1. STATE DEFINITIONS ---
    var gender by remember { mutableStateOf("M") }
    var ageInput by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("") }
    var vomit by remember { mutableStateOf("") }
    var diapers by remember { mutableStateOf("") }
    var appetite by remember { mutableStateOf(3f) }
    var stoolFreq by remember { mutableStateOf("") }
    var stoolColor by remember { mutableStateOf("Coklat") }

    // Gejala States
    var symptomCough by remember { mutableStateOf(false) }
    var symptomRash by remember { mutableStateOf(false) }
    var symptomFlu by remember { mutableStateOf(false) }
    var symptomDifficultBreath by remember { mutableStateOf(false) }
    var symptomHardToNurse by remember { mutableStateOf(false) }

    // --- 2. LOGIC & VIEWMODEL ---
    val isFormValid = remember(ageInput, temp, vomit, diapers, stoolFreq) {
        ageInput.isNotBlank() && temp.isNotBlank() && vomit.isNotBlank()
                && diapers.isNotBlank() && stoolFreq.isNotBlank()
    }

    val history by vm.history.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val isEndOfList by vm.isEndOfList.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()

    // Fungsi Submit
    fun performAnalysis() {
        if (!isFormValid) return

        val symptoms = mutableListOf<String>().apply {
            if (symptomCough) add("Batuk")
            if (symptomRash) add("Ruam")
            if (symptomFlu) add("Flu")
            if (symptomDifficultBreath) add("Sesak Nafas")
            if (symptomHardToNurse) add("Sulit Menyusu")
        }

        val input = HealthCheckInput(
            gender, ageInput.toIntOrNull() ?: 0, temp.toDoubleOrNull(),
            vomit.toIntOrNull() ?: 0, diapers.toIntOrNull() ?: 0,
            appetite.toInt(), stoolFreq.toIntOrNull() ?: 0, stoolColor, symptoms
        )

        navController.currentBackStackEntry?.savedStateHandle?.apply {
            set("isHistoryView", false)
            set("healthInput", input)
        }
        navController.navigate(Screen.Result.route)
    }

    // --- 3. SIDE EFFECTS ---
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val returnedSummary = savedStateHandle?.getLiveData<HealthCheckSummary>("healthResult")?.observeAsState()?.value

    LaunchedEffect(returnedSummary) {
        returnedSummary?.let { summary ->
            vm.addHistory(summary)
            savedStateHandle.remove<HealthCheckSummary>("healthResult")
        }
    }

    LaunchedEffect(Unit) { vm.fetchHistory() }

    // --- 4. MAIN LAYOUT ---
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        SimpleHeader(title = "Cek Kesehatan", onBackClick = { navController.popBackStack() })

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Form Sections
            item { IdentitySection(gender, { gender = it }, ageInput, { ageInput = it }) }
            item { VitalSection(temp, { temp = it }, vomit, { vomit = it }, diapers, { diapers = it }) }
            item { AppetiteSection(appetite, { appetite = it }) }
            item { DigestionSection(stoolFreq, { stoolFreq = it }, stoolColor, { stoolColor = it }) }

            item {
                SymptomsSection(
                    symptomCough, { symptomCough = it },
                    symptomRash, { symptomRash = it },
                    symptomFlu, { symptomFlu = it },
                    symptomDifficultBreath, { symptomDifficultBreath = it },
                    symptomHardToNurse, { symptomHardToNurse = it }
                )
            }

            // Action Button
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SubmitCheckButton(isValid = isFormValid, onClick = { performAnalysis() })
            }

            // History Section
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