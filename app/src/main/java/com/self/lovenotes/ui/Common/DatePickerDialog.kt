package com.self.lovenotes.ui.Common

import BasicPagerCalendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog (
    selectedDate: LocalDate,
    onDissmiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit = {}
) {
    var localDate by remember { mutableStateOf(selectedDate) }

    Dialog(
        onDismissRequest = onDissmiss,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Column {
                BasicPagerCalendar(
                    selectedDate = localDate,
                    onDateSelected= {localDate = it}
                )

                Row (
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ){
                    TextButton(
                        onClick = { onDateSelected(localDate); onDissmiss() },
                        modifier = Modifier.width(95.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Text(
                            text = "Confirm",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    TextButton(
                        onClick = onDissmiss,
                        modifier = Modifier.width(95.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}