package com.felipeserver.site.glyph.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.felipeserver.site.glyph.ui.theme.GlyphTheme
import com.felipeserver.site.glyph.ui.viewmodel.NoteUiState
import com.felipeserver.site.glyph.ui.viewmodel.NoteViewModel

@Composable
fun NoteView(id: String?, navController: NavController) {

    val viewModel: NoteViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(id) {

        id?.toIntOrNull()?.let {

            viewModel.getNoteById(it)
        }
    }


    NoteViewContent(
        uiState = uiState,
        onTitleChange = viewModel::updateTitle,
        onContentChange = viewModel::updateContent,
        onBack = {
            if(uiState.title.isNotEmpty() || uiState.content.isNotEmpty()){
                viewModel.saveNote()
            }
            navController.popBackStack()
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteViewContent(
    uiState: NoteUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onBack: () -> Unit
) {
    val transparentTextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    )

    Scaffold(
        modifier = Modifier.fillMaxHeight(),

        topBar = {
            TopAppBar(

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),

                title = { },

                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.title,
                onValueChange = onTitleChange,
                placeholder = {
                    Text(
                        text = "Título",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                textStyle = MaterialTheme.typography.titleLarge,
                colors = transparentTextFieldColors
            )

            TextField(
                modifier = Modifier.fillMaxSize(),
                value = uiState.content,
                onValueChange = onContentChange,
                placeholder = {
                    Text(
                        text = "Comece a escrever...",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                colors = transparentTextFieldColors
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NoteViewPreview() {

    GlyphTheme {
        NoteViewContent(
            uiState = NoteUiState(
                title = "Título de Exemplo", content = "Conteúdo da nota de exemplo..."
            ), onTitleChange = {}, onContentChange = {}, onBack = {})
    }
}
