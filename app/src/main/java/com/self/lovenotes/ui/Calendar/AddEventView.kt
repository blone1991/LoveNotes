package com.self.lovenotes.ui.Calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddEventScreen(
    date: String,
    onAdd: (String) -> Unit,
    onClose: () -> Unit,
) {
    var eventTitle by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = onClose,
    ) {
        Surface(
            modifier = Modifier,
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceBright,
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Add Event for $date",
                    style = MaterialTheme.typography.headlineLarge
                )
                OutlinedTextField(
                    value = eventTitle,
                    onValueChange = { eventTitle = it },
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        if (eventTitle.isNotEmpty()) {
                            onAdd(eventTitle)
                            onClose();
                        }
                    },
                ) {
                    Text("Save")
                }
            }
        }
    }
}