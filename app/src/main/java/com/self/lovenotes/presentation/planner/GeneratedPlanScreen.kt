package com.self.lovenotes.presentation.planner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun GeneratedPlanScreen(
    viewModel: PlannerViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val plan by viewModel.generatedPlan.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Schedule Recommendation for You",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            MarkdownText(
                markdown = plan,
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(
                onClick = {
                    viewModel.clearPlan()
                    onDismiss()
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(androidx.compose.ui.Alignment.End)
            ) {
                Text("Close")
            }
        }
    }
}