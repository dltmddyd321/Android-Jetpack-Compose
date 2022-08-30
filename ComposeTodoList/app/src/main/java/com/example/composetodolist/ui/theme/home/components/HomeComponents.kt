package com.example.composetodolist.ui.theme.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composetodolist.Todo

@Composable
fun TodoItem(
    todo: Todo,
    onChecked: (Boolean) -> Unit,
    onDelete: (Todo) -> Unit,
    onNavigation: (Todo) -> Unit
) {
    Card(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                onNavigation(todo)
            }
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = todo.isComplete, onCheckedChange = { onChecked(it) })
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = todo.todo, style = MaterialTheme.typography.subtitle2)
                //현재 블록 내의 모든 하위 컴포저블에 데이터를 전달
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = todo.time, style = MaterialTheme.typography.body2)
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            IconButton(onClick = { onDelete(todo) }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
            }
        }
    }
}