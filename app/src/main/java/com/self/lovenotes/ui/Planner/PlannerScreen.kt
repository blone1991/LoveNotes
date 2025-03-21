package com.self.lovenotes.ui.Planner

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.self.lovenotes.ui.Common.DatePickerDialog
import com.self.lovenotes.ui.Common.LoadingIndicatorDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel(),
){
    val uiState by viewModel.uiState.collectAsState()
    var date by remember { mutableStateOf(LocalDate.now()) }
    var datePickerDialogState by remember { mutableStateOf(false) }

    var location by remember { mutableStateOf("") }
    var member by remember { mutableStateOf("0") }
    var considerations by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Date Planner",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "We recommend your daily schedule. Please provide the information below for your recommendation.",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(13.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    "Date",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedTextField (
                    value = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    onValueChange = {},
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            if (it.hasFocus) {
                                datePickerDialogState = true
                            }
                        }
                        .focusable()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Location",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("enter a location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Members",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                OutlinedTextField(
                    value = member,
                    onValueChange = { newText ->
                        member = newText.toIntOrNull()?.let {
                            if (it in 0..999) it.toString() else member
                        } ?: "0"
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("number of people") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Additional considerations",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                OutlinedTextField(
                    value = considerations,
                    onValueChange = { considerations = it},
                    label = { Text(text = "considerations")},
                    placeholder = { Text("Please add any additional considerations\n" +
                            "(ex. purpose, relationship or etc)") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {viewModel.requestPlan(
                        date = date,
                        members = member.toInt(),
                        location = location,
                        considerations = considerations
                    )},
                ) {
                    Text("Submit")
                }
            }
        }
    }

    if (datePickerDialogState) {
        DatePickerDialog (
            selectedDate = date,
            onDissmiss = {datePickerDialogState = false},
            onDateSelected = { date = it }
        )
    }

    when (uiState) {
        is UiState.Loading -> { LoadingIndicatorDialog(color = MaterialTheme.colorScheme.primary) }
        is UiState.Success -> { GeneratedPlanDialog(text = (uiState as UiState.Success).plan, onDismiss = viewModel::closePlanDialog) }
        else -> {}
    }
}