package com.felipeserver.site.glyph.ui.screen

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
import com.felipeserver.site.glyph.ui.theme.GlyphTheme
import com.felipeserver.site.glyph.ui.viewmodel.NoteUiState
import com.felipeserver.site.glyph.ui.viewmodel.NoteViewModel

/**
 * @Composable é a anotação que transforma uma função em um componente de UI no Jetpack Compose.
 * A função `NoteView` representa a tela de visualização e edição de uma única nota.
 * Esta é a versão "stateful" do Composable, que gerencia o estado.
 */
@Composable
fun NoteView(id: String?, navController: NavController) {
    // Obtém uma instância do NoteViewModel. O ciclo de vida do ViewModel
    // será gerenciado automaticamente pelo Compose.
    val viewModel: NoteViewModel = viewModel()

    // Observa o `uiState` do ViewModel. `collectAsState` converte o StateFlow do ViewModel
    // em um State do Compose. Sempre que o `uiState` no ViewModel for atualizado,
    // esta tela será "recomposta" (redesenhada) com os novos dados.
    val uiState by viewModel.uiState.collectAsState()

    // `LaunchedEffect` é usado para executar uma função de suspensão (coroutine)
    // de forma segura dentro do ciclo de vida de um Composable. Ele será executado
    // sempre que o valor de `id` mudar.
    LaunchedEffect(id) {
        // Verifica se o `id` não é nulo e tenta convertê-lo para um inteiro.
        id?.toIntOrNull()?.let {
            // Se for um id válido, chama a função do ViewModel para carregar a nota correspondente.
            viewModel.getNoteById(it)
        }
    }

    // Chama a versão "stateless" do Composable, passando o estado e os callbacks.
    NoteViewContent(
        uiState = uiState,
        onTitleChange = viewModel::updateTitle,
        onContentChange = viewModel::updateContent,
        onBack = {
            viewModel.saveNote()
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

    Scaffold(
        modifier = Modifier.fillMaxHeight(),

        topBar = {
            TopAppBar(

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
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
        ) {

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.title,
                onValueChange = onTitleChange,
                placeholder = {
                    Text(
                        text = "Title", style = MaterialTheme.typography.titleLarge
                    )
                },

                textStyle = TextStyle(fontSize = MaterialTheme.typography.titleLarge.fontSize)
            )

            TextField(
                modifier = Modifier.fillMaxSize(),
                value = uiState.content,
                onValueChange = onContentChange,
                placeholder = {
                    Text(text = "Note")
                },
                textStyle = TextStyle(fontSize = 24.sp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NoteViewPreview() {

    GlyphTheme {
        NoteViewContent(
            uiState = NoteUiState(
            title = "Título de Exemplo", content = "Conteúdo da nota de exemplo..."
        ), onTitleChange = {}, onContentChange = {}, onBack = {})
    }
}
