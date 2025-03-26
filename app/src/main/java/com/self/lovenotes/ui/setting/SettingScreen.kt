package com.self.lovenotes.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val users by viewModel.users.collectAsState()

    var inputNickname by remember { mutableStateOf("") }
    var inputInvitationCode by remember { mutableStateOf("") }

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
                if (users.values.toList()[0].nickname.isNotEmpty()) {
                    Text(
                        "Your invitation code",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        modifier = Modifier
                            .padding(20.dp)
                            .align(Alignment.CenterHorizontally),
                        text = users.values.toList()[0].invitationCode,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                } else {
                    Text(
                        "Set Nickname",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        "After you enter your nickname, you will see an invitation code.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )

                    OutlinedTextField(
                        value = inputNickname,
                        onValueChange = { inputNickname = it },
                        label = { Text("nickname") },
                        placeholder = { Text("please enter your name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                            if (inputNickname.isNotEmpty())
                                viewModel.updateNickname(inputNickname)

                            inputNickname = ""
                        },
                    ) {
                        Text("Confirm")
                    }
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
                    "Subscribe",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge
                )
                OutlinedTextField(
                    value = inputInvitationCode,
                    onValueChange = { inputInvitationCode = it },
                    label = { Text("other's invitation code") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        if (inputInvitationCode.isNotEmpty())
                            viewModel.subscribe(inputInvitationCode)

                        inputInvitationCode = ""
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
                modifier = Modifier
                    .padding(13.dp)
                    .defaultMinSize(minHeight = 150.dp)
            ) {
                Text(
                    "on SubScribing",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge
                )

                if (users.values.size > 1) {
                    LazyColumn(
                        userScrollEnabled = true,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(users.values.toList().drop(0)) {
                            NickNameCard(
                                modifier = Modifier,
                                user = it,
                                onDelete = { viewModel.deleteSubscribe(it) },
                            )
                        }
                    }
                } else {
                    Text(
                        text = "There are no subscribed users.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }


}