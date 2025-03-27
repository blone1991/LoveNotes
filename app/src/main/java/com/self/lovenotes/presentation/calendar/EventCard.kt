package com.self.lovenotes.presentation.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.self.lovenotes.data.remote.model.Event
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.Clock

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    event: Event,
    author: String,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {

            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceBright,
                        shape = MaterialTheme.shapes.small,
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "")
                        Text(
                            text = "${if (author.isEmpty()) "Unknown" else author}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Tabler.Outline.Clock, contentDescription = "")
                        Text(
                            text = "${
                                if (event.fullday)
                                    "Full Day"
                                else event.startTime.substring(
                                    0,
                                    2
                                ) + ":" + event.startTime.substring(2, 4)
                                        + "~" + event.endTime.substring(
                                    0,
                                    2
                                ) + ":" + event.endTime.substring(2, 4)
                            }",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Edit")
                    }
                }
            }

            if (event.location != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Place, contentDescription = "")
                    Text(
                        text = "${event.location.split("/")[0]}",
                        style = TextStyle().copy(fontSize = 10.sp)
                    )
                }
            }
        }


    }

}