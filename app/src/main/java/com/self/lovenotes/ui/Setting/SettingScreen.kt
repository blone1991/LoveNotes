package com.self.lovenotes.ui.Setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val user by viewModel.myInviteCode.collectAsState()
    val inputValue = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(13.dp)
            ) {
                Text(
                    "My InviteCode",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally),
                    text = user.inviteCode,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(13.dp)
            ) {
                Text(
                    "Input InviteCode",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge
                )
                OutlinedTextField(
                    value = inputValue.value,
                    onValueChange = { inputValue.value = it },
                    label = { Text("other's invite Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        if (inputValue.value.isNotEmpty())
                            viewModel.addSubscribe(inputValue.value)

                        inputValue.value = "";
                    },
                ) {
                    Text("Save")
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(13.dp)
            ) {
                Text(
                    "on SubScribing",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge
                )
                Column {
                    user.subscribing.forEach {
                        Text(text = it)
                    }
                }
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = viewModel::clearSubscrible,
                ) {
                    Text("Clear")
                }
            }
        }
    }


}