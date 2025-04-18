package com.self.lovenotes.presentation.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.self.lovenotes.presentation.common.DatePickerDialog
import com.self.lovenotes.presentation.common.LoadingIndicatorDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var date by remember { mutableStateOf(LocalDate.now()) }
    var datePickerDialogState by remember { mutableStateOf(false) }

    var location by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var considerations by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    when (uiState) {
        is UiState.Loading -> {
            LoadingIndicatorDialog(color = MaterialTheme.colorScheme.primary)
            return
        }
        is UiState.Success -> {
            GeneratedPlanScreen(markDownText = (uiState as UiState.Success).plan, viewModel::resetUiState)
            return
        }
        else -> {}
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
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
            ) {
                Text(
                    "Date",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedTextField(
                    value = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    onValueChange = {},
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.hasFocus) {
                                datePickerDialogState = true
                                focusRequester.freeFocus()
                            }
                        }
                        .focusable(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = ""
                        )
                    }
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
                    isError = location.isEmpty(),
                    label = { Text("enter a location") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "location"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Purpose",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    isError = purpose.isEmpty(),
                    label = { Text("purpose of meeting") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "purpose"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Additional considerations",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                OutlinedTextField(
                    value = considerations,
                    onValueChange = { considerations = it },
                    label = { Text(text = "considerations") },
                    placeholder = {
                        Text(
                            "Please add any additional considerations\n" +
                                    "(ex. purpose, relationship or etc)"
                        )
                    },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "notification"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier.align(Alignment.End),
                    enabled = location.isNotEmpty() && purpose.isNotEmpty(),
                    onClick = {
                        viewModel.requestPlan(
                            date = date,
                            purpose = purpose,
                            location = location,
                            considerations = considerations
                        )
                    },
                ) {
                    Text("Submit")
                }
            }
        }
    }

    if (datePickerDialogState) {
        DatePickerDialog(
            selectedDate = date,
            onDismiss = { datePickerDialogState = false },
            onDateSelected = { date = it }
        )
    }
}