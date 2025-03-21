package com.self.lovenotes.ui.Calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.self.lovenotes.data.model.Event

@Composable
fun EventCard (
    modifier: Modifier = Modifier,
    event: Event,
    author: String
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        Column (
            modifier = Modifier.padding(5.dp)
        ){

            Column (
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceBright,
                    shape = MaterialTheme.shapes.small,
                    ).fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ){
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Text(
                text = "Author : ${author}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "Time : ${
                    if (event.fullday) 
                        "Full Day" 
                    else event.startTime.substring(0, 2) + ":" + event.startTime.substring(2, 4) 
                            + "~" + event.endTime.substring(0, 2) + ":" + event.endTime.substring(2, 4)
                }",
                style = MaterialTheme.typography.labelLarge
            )
            if (event.location != null) {
                Text(
                    text = "Location : ${event.location}",
                    style = MaterialTheme.typography.labelLarge
                )

            }
        }



    }

}