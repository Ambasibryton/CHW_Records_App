package com.example.chw_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID


private val FormTeal = Color(0xFF087F8C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitForm(

    onSave: (VisitRecord) -> Unit,
    onCancel: () -> Unit
) {
    var chwId by remember { mutableStateOf("CHW001") }
    var patientName by remember { mutableStateOf("") }
    var villageName by remember { mutableStateOf("") }
    var visitType by remember { mutableStateOf("") }
    var bloodPressure by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "New Patient Visit",
            style = androidx.compose.material3.MaterialTheme
                .typography.headlineSmall
        )

        Text("Enter the patient's visit details.")

        OutlinedTextField(
            value = chwId,
            onValueChange = { chwId = it },
            label = { Text("CHW ID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = patientName,
            onValueChange = { patientName = it },
            label = { Text("Patient name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = villageName,
            onValueChange = { villageName = it },
            label = { Text("Village name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        FormDropdown(
            label = "Visit type *",
            options = listOf("Maternal", "Child", "General"),
            selected = visitType,
            onSelected = { visitType = it }
        )

        OutlinedTextField(
            value = bloodPressure,
            onValueChange = { bloodPressure = it },
            label = { Text("Blood pressure (e.g. 120/80) *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = temperature,
            onValueChange = { temperature = it },
            label = { Text("Temperature °C *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        FormDropdown(
            label = "Urgency level *",
            options = listOf("Routine", "Urgent", "Emergency"),
            selected = urgency,
            onSelected = { urgency = it }
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Visit notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = Color(0xFFB3261E)
            )
        }

        Button(
            onClick = {
                val parsedTemperature = temperature.toDoubleOrNull()

                errorMessage = when {
                    chwId.isBlank() ->
                        "Enter the CHW ID."

                    patientName.trim().length < 2 ->
                        "Patient name must contain at least 2 characters."

                    villageName.isBlank() ->
                        "Enter the village name."

                    visitType !in listOf("Maternal", "Child", "General") ->
                        "Select a valid visit type."

                    !bloodPressure.trim().matches(
                        Regex("""\d{2,3}/\d{2,3}""")
                    ) ->
                        "Enter blood pressure as SYS/DIA, e.g. 120/80."

                    parsedTemperature == null ||
                            parsedTemperature !in 35.0..43.0 ->
                        "Temperature must be between 35.0 and 43.0 °C."

                    urgency !in listOf("Routine", "Urgent", "Emergency") ->
                        "Select an urgency level."

                    else -> ""
                }

                if (errorMessage.isEmpty()) {
                    val timestamp = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss'Z'",
                        Locale.US
                    ).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }.format(Date())

                    onSave(
                        VisitRecord(
                            localId = UUID.randomUUID().toString(),
                            chwId = chwId.trim(),
                            patientName = patientName.trim(),
                            villageName = villageName.trim(),
                            visitType = visitType,
                            vitalsBp = bloodPressure.trim(),
                            vitalsTemp = parsedTemperature!!,
                            urgencyLevel = urgency,
                            visitNotes = notes.trim().ifEmpty { null },
                            createdAtLocal = timestamp,
                            syncStatus = "Pending"
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FormTeal
            )
        ) {
            Text("Save Visit")
        }

        Button(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE6EEEE),
                contentColor = Color(0xFF183238)
            )
        ) {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
