package com.felipeserver.site.glyph.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.felipeserver.site.glyph.ui.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteView(id: String?, navController: NavController) {
    val viewModel: NoteViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Load the note if an ID is provided
    LaunchedEffect(id) {
        id?.toIntOrNull()?.let {
            viewModel.getNoteById(it)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxHeight(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { /* Title can be dynamic if needed */ },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveNote()
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                placeholder = {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                textStyle = TextStyle(fontSize = MaterialTheme.typography.titleLarge.fontSize)
            )
            TextField(
                modifier = Modifier.fillMaxSize(),
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                placeholder = {
                    Text(text = "Note")
                },
                textStyle = TextStyle(fontSize = 24.sp)
            )
        }
    }
}

@Composable
@Preview
fun NoteViewPreview() {
    NoteView("1", navController = rememberNavController())
}
