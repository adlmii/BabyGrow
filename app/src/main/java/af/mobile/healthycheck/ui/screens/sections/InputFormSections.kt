package af.mobile.healthycheck.ui.screens.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import af.mobile.healthycheck.ui.components.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.shadow

// 1. Identity Section
@Composable
fun IdentitySection(
    gender: String,
    onGenderChange: (String) -> Unit,
    ageInput: String,
    onAgeChange: (String) -> Unit
) {
    ModernCard(title = "Identitas Anak", icon = Icons.Outlined.Person) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GenderButton(
                text = "Laki-laki",
                icon = Icons.Outlined.Male,
                selected = gender == "M",
                modifier = Modifier.weight(1f)
            ) { onGenderChange("M") }

            GenderButton(
                text = "Perempuan",
                icon = Icons.Outlined.Female,
                selected = gender == "F",
                modifier = Modifier.weight(1f)
            ) { onGenderChange("F") }
        }
        ModernTextField(
            value = ageInput,
            onValueChange = { if (it.all { char -> char.isDigit() }) onAgeChange(it) },
            label = "Umur Anak",
            placeholder = "Contoh: 12",
            suffix = "Bulan",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Outlined.Cake
        )
    }
}

// 2. Vital Section
@Composable
fun VitalSection(
    temp: String, onTempChange: (String) -> Unit,
    vomit: String, onVomitChange: (String) -> Unit,
    diapers: String, onDiapersChange: (String) -> Unit
) {
    ModernCard(title = "Tanda Vital", icon = Icons.Outlined.MonitorHeart) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ModernTextField(value = temp, onValueChange = onTempChange, label = "Suhu Tubuh", placeholder = "Contoh: 36.5", suffix = "°C", keyboardType = KeyboardType.Decimal, leadingIcon = Icons.Outlined.Thermostat)
            ModernTextField(value = vomit, onValueChange = onVomitChange, label = "Muntah", suffix = "x / hari", keyboardType = KeyboardType.Number, leadingIcon = Icons.Outlined.Sick)
            ModernTextField(value = diapers, onValueChange = onDiapersChange, label = "Popok Basah", suffix = "x / hari", keyboardType = KeyboardType.Number, leadingIcon = Icons.Outlined.WaterDrop)
        }
    }
}

// 3. Appetite Section
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppetiteSection(
    appetite: Float,
    onAppetiteChange: (Float) -> Unit
) {
    ModernCard(title = "Nafsu Makan", icon = Icons.Outlined.Restaurant) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Tingkat Nafsu Makan", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)) {
                    Text("${appetite.toInt()}/5", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelLarge, color = Color.White)
                }
            }
                Slider(
                value = appetite, onValueChange = onAppetiteChange, valueRange = 1f..5f, steps = 3, modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant),
                thumb = { Box(modifier = Modifier.size(32.dp).shadow(4.dp, CircleShape).background(MaterialTheme.colorScheme.primary, CircleShape).border(4.dp, Color.White, CircleShape)) }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tidak Mau", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                Text("Sangat Lahap", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// 4. Digestion Section
// [PERBAIKAN] Pastikan @OptIn ada di sini untuk mengatasi error "line 91"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigestionSection(
    stoolFreq: String, onStoolFreqChange: (String) -> Unit,
    stoolColor: String, onStoolColorChange: (String) -> Unit
) {
    ModernCard(title = "Pencernaan (BAB)", icon = Icons.Outlined.Spa) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModernTextField(value = stoolFreq, onValueChange = onStoolFreqChange, label = "Frekuensi", placeholder = "0", suffix = "x / hari", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))

            var expandedColor by remember { mutableStateOf(false) }
            val colors = listOf("Coklat", "Kuning", "Hijau", "Putih Pucat", "Hitam", "Berdarah")

            // Komponen Eksperimental ada di sini
            ExposedDropdownMenuBox(
                expanded = expandedColor,
                onExpandedChange = { expandedColor = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = stoolColor, onValueChange = {}, label = { Text("Warna") }, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedColor) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                )
                ExposedDropdownMenu(expanded = expandedColor, onDismissRequest = { expandedColor = false }) {
                    colors.forEach { color -> DropdownMenuItem(text = { Text(color) }, onClick = { onStoolColorChange(color); expandedColor = false }) }
                }
            }
        }
    }
}

// 5. Symptoms Section
@Composable
fun SymptomsSection(
    cough: Boolean, onCoughChange: (Boolean) -> Unit,
    rash: Boolean, onRashChange: (Boolean) -> Unit,
    flu: Boolean, onFluChange: (Boolean) -> Unit,
    breath: Boolean, onBreathChange: (Boolean) -> Unit,
    nurse: Boolean, onNurseChange: (Boolean) -> Unit
) {
    ModernCard(title = "Gejala Tambahan", icon = Icons.Outlined.WarningAmber) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) { SymptomCheckbox("Batuk", cough, onCoughChange) }
                Box(modifier = Modifier.weight(1f)) { SymptomCheckbox("Ruam Kulit", rash, onRashChange) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) { SymptomCheckbox("Flu / Pilek", flu, onFluChange) }
                Box(modifier = Modifier.weight(1f)) { SymptomCheckbox("Sesak Napas", breath, onBreathChange) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) { SymptomCheckbox("Sulit Menyusu", nurse, onNurseChange) }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}