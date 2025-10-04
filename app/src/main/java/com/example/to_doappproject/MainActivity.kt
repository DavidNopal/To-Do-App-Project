package com.example.to_doappproject

import android.os.Bundle
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.to_doappproject.ui.theme.ToDoAppProjectTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class TodoItem(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isDone: Boolean = false
)

class TodoViewModel : ViewModel() {
    private val _todos = mutableStateListOf<TodoItem>()
    val todos: List<TodoItem> get() = _todos


    fun addTodo(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        _todos.add(TodoItem(text = trimmed))
    }

    fun toggleTodo(id: Long) {
        val idx = _todos.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val current = _todos[idx]
            _todos[idx] = current.copy(isDone = !current.isDone)
        }
    }

    fun removeTodo(id: Long) {
        _todos.removeAll { it.id == id }
    }

    fun editTodo(id: Long, newText: String) {
        val trimmed = newText.trim()
        if (trimmed.isEmpty()) return
        val idx = _todos.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val current = todos[idx]
            _todos[idx] = current.copy(text = trimmed)
        }
    }
}

@Composable
fun TodoApp(
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = viewModel()
) {
    val todos = viewModel.todos

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "To-Do App",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TodoInput(onAdd = { viewModel.addTodo(it) })

        Spacer(modifier = Modifier.height(24.dp))

        val activeTodos = todos.filter { !it.isDone }
        val completedTodos = todos.filter { it.isDone }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (activeTodos.isNotEmpty()) {
                item {
                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(activeTodos) { todo ->
                    TodoItemRow(
                        todo = todo,
                        onToggle = { viewModel.toggleTodo(it) },
                        onDelete = { viewModel.removeTodo(it) }
                    )
                }
            } else {
                item {
                    Text(
                        text = "No items yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            if (completedTodos.isNotEmpty()) {
                item {
                    Text(
                        text = "Completed Items",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(completedTodos) { todo ->
                    TodoItemRow(
                        todo = todo,
                        onToggle = { viewModel.toggleTodo(it) },
                        onDelete = { viewModel.removeTodo(it) }
                    )
                }
            } else if (todos.isNotEmpty()) {
                item {
                    Text(
                        text = "No completed items yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun TodoInput(
    onAdd: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable { mutableStateOf("") }
    var showValidation by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    if (showValidation) showValidation = false
                },
                placeholder = { Text("Add task") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                val trimmed = text.trim()
                if (trimmed.isEmpty()) {
                    showValidation = true
                } else {
                    onAdd(trimmed)
                    text = ""
                    showValidation = false
                }
            }) {
                Text("Add")
            }
        }
        if (showValidation) {
            Text(
                text = "You must enter a task",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun TodoItemRow(
    todo: TodoItem,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize()
    ) {
        androidx.compose.material3.Checkbox(
            checked = todo.isDone,
            onCheckedChange = { onToggle(todo.id) }
        )

        Text(
            text = todo.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        )

        androidx.compose.material3.IconButton(onClick = { onDelete(todo.id) }) {
            androidx.compose.material3.Text("❌")
        }
    }
}

@Preview
    (showBackground = true)
@Composable
fun TodoAppPreview() {
    ToDoAppProjectTheme {
        TodoApp()
    }
}