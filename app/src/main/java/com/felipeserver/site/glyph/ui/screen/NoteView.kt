package com.felipeserver.site.glyph.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.felipeserver.site.glyph.data.local.TagEntity
import com.felipeserver.site.glyph.ui.theme.GlyphTheme
import com.felipeserver.site.glyph.ui.viewmodel.NoteUiState
import com.felipeserver.site.glyph.ui.viewmodel.NoteViewModel
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.MarkdownPadding


@Composable
fun NoteView(id: String?, navController: NavController) {

    val viewModel: NoteViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsState()
    val allTags by viewModel.allTags.collectAsState()

    LaunchedEffect(id) {
        id?.toIntOrNull()?.let {
            viewModel.getNoteWithTags(it)
        }
    }


    NoteViewContent(
        uiState = uiState,
        allTags = allTags,
        onTitleChange = viewModel::updateTitle,
        onContentChange = viewModel::updateContent,
        onTagSelected = { tag, isSelected ->
            if (isSelected) {
                viewModel.addTagToCurrentNote(tag)
            } else {
                viewModel.removeTagFromCurrentNote(tag)
            }
        },
        onCreateTag = viewModel::createTag,
        onDeleteTag = viewModel::deleteTag,
        onBack = {
            if (uiState.title.isNotEmpty() || uiState.content.isNotEmpty()) {
                viewModel.saveNote()
            }
            navController.popBackStack()
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteViewContent(
    uiState: NoteUiState,
    allTags: List<TagEntity>,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onTagSelected: (TagEntity, Boolean) -> Unit,
    onCreateTag: (String) -> Unit,
    onDeleteTag: (TagEntity) -> Unit,
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

    var showNewTagDialog by remember { mutableStateOf(false) }
    var tagToDelete by remember { mutableStateOf<TagEntity?>(null) }

    if (showNewTagDialog) {
        NewTagDialog(
            onDismiss = { showNewTagDialog = false },
            onConfirm = {
                onCreateTag(it)
                showNewTagDialog = false
            }
        )
    }

    tagToDelete?.let { tag ->
        AlertDialog(
            onDismissRequest = { tagToDelete = null },
            title = { Text("Excluir Tag") },
            text = { Text("Tem certeza que deseja excluir a tag \"${tag.name}\"? Todas as notas que usam esta tag perderão a associação.") },            confirmButton = {
                TextButton(onClick = {
                    onDeleteTag(tag)
                    tagToDelete = null
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { tagToDelete = null }) { Text("Cancelar") }
            }
        )
    }

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
                },
                actions = {
                    var menuExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = "Gerenciar Tags"
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            allTags.forEach { tag ->
                                val isSelected = uiState.tags.any { it.tagId == tag.tagId }
                                DropdownMenuItem(
                                    text = { Text(tag.name, textAlign = TextAlign.Start) },
                                    onClick = {
                                        onTagSelected(tag, !isSelected)
                                    },
                                    leadingIcon = {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = "Selecionada"
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { tagToDelete = tag }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Excluir tag"
                                            )
                                        }
                                    }
                                )
                            }
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Criar nova tag") },
                                onClick = {
                                    menuExpanded = false
                                    showNewTagDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Criar nova tag"
                                    )
                                }
                            )
                        }
                    }
                }
            )
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

            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(uiState.tags) { tag ->
                    AssistChip(onClick = { }, label = { Text(tag.name) })
                }
            }

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
@Composable
fun NoteViewMD(content: String) {
    Scaffold { innerPadding ->
        Markdown(
            content = content,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NoteViewMDPreview()
{
    GlyphTheme {
        NoteViewMD(
            content = "**Bold text**"
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTagDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Tag") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nome da Tag") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text)
                    }
                }
            ) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}




@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NoteViewPreview() {
    val sampleTags = listOf(TagEntity(1, "Life"), TagEntity(2, "Work"))
    GlyphTheme {
        NoteViewContent(
            uiState = NoteUiState(
                title = "Título de Exemplo",
                content = "Conteúdo da nota de exemplo...",
                tags = listOf(sampleTags[0])
            ),
            allTags = sampleTags,
            onTitleChange = {},
            onContentChange = {},
            onTagSelected = { _, _ -> },
            onCreateTag = {},
            onDeleteTag = {},
            onBack = {}
        )
    }
}
