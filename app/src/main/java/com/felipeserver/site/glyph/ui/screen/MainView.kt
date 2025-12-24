package com.felipeserver.site.glyph.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.felipeserver.site.glyph.R
import com.felipeserver.site.glyph.data.local.NoteEntity
import com.felipeserver.site.glyph.data.local.NoteWithTags
import com.felipeserver.site.glyph.data.local.TagEntity
import com.felipeserver.site.glyph.navigation.Screen
import com.felipeserver.site.glyph.ui.theme.GlyphTheme
import com.felipeserver.site.glyph.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainViewScreen(navController: NavController, viewModel: NoteViewModel = viewModel()) {
    val notesWithTags by viewModel.notesWithTags.collectAsState()
    val allTags by viewModel.allTags.collectAsState()
    val scope = rememberCoroutineScope()
    MainView(
        notes = notesWithTags,
        tags = allTags,
        navController = navController,
        onFabClick = {
            scope.launch {
                val lastId = viewModel.getLastNoteId() ?: 0
                val newId = lastId + 1
                navController.navigate(Screen.Note.withArgs(newId.toString()))
            }
        },
        onDeleteNote = { note ->
            viewModel.deleteNote(note)
        }
    )
}


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MainView(
    notes: List<NoteWithTags> = emptyList(),
    tags: List<TagEntity> = emptyList(),
    navController: NavController? = null,
    onFabClick: () -> Unit,
    onDeleteNote: (NoteEntity) -> Unit
) {
    val topScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val fraction = topScrollBehavior.state.collapsedFraction
    val topAppBarFontSize = lerp(
        MaterialTheme.typography.displayLargeEmphasized.fontSize,
        MaterialTheme.typography.headlineLargeEmphasized.fontSize,
        fraction
    )


    val bottomScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var selectedTags by remember { mutableStateOf<Set<TagEntity>>(emptySet()) }

    val filteredNotes = remember(notes, searchQuery, selectedTags) {
        val textFiltered = if (searchQuery.isEmpty()) {
            notes
        } else {
            notes.filter { noteWithTags ->
                noteWithTags.note.title.contains(searchQuery, ignoreCase = true)
                        ||
                        noteWithTags.note.content.contains(searchQuery, ignoreCase = true)
            }
        }

        if (selectedTags.isEmpty()) {
            textFiltered
        } else {
            textFiltered.filter { noteWithTags ->
                val noteTagIds = noteWithTags.tags.map { it.tagId }.toSet()
                selectedTags.all { selectedTag -> noteTagIds.contains(selectedTag.tagId) }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topScrollBehavior.nestedScrollConnection)
            .nestedScroll(bottomScrollBehavior.nestedScrollConnection),

        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ), title = {
                    Text(
                        stringResource(R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = topAppBarFontSize,
                        style = MaterialTheme.typography.displayLargeEmphasized
                    )
                }, scrollBehavior = topScrollBehavior
            )

        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                scrollBehavior = bottomScrollBehavior,

                ) {
                Row(
                    modifier = Modifier.background(Color.Transparent),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    FloatingActionButton(
                        onClick = onFabClick,
                        shape = FloatingActionButtonDefaults.shape,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 16.dp,
                            focusedElevation = 16.dp,
                            hoveredElevation = 16.dp


                        )
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_note_cd)
                        )
                    }
                    Spacer(modifier = Modifier.padding(start = 16.dp))
                    DockedSearchBar(
                        modifier = Modifier
                            .weight(1f),
                        shadowElevation = 8.dp,
                        tonalElevation = 8.dp,
                        query = searchQuery,
                        shape = RoundedCornerShape(50),
                        onQueryChange = { searchQuery = it },
                        onSearch = { active = false }, // Keep this to define search action
                        active = active,
                        onActiveChange = { active = false },
                        placeholder = { Text(stringResource(R.string.search_hint)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.clear_search_cd)
                                    )
                                }
                            }
                        }
                    ) {
                        // Content for the expanded search results, e.g., a list of suggestions
                    }
                }
            }
        }) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags) { tag ->
                    val isSelected = selectedTags.contains(tag)
                    NoteChips(
                        label = tag.name,
                        isSelected = isSelected,
                        onSelectedChange = {
                            selectedTags = if (isSelected) {
                                selectedTags - tag
                            } else {
                                selectedTags + tag
                            }
                        }
                    )
                }
            }
            LazyColumn {
                items(
                    items = filteredNotes,
                    key = { noteWithTags -> noteWithTags.note.id }
                ) { individualNoteWithTags ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                onDeleteNote(individualNoteWithTags.note)
                                true 
                            } else {
                                false
                            }
                        }
                    )
                    
                    SwipeToDismissBox(
                        modifier = Modifier.animateItem(),
                        state = dismissState,
                        backgroundContent = {
                            val color = when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.Settled -> Color.Transparent
                                else -> MaterialTheme.colorScheme.errorContainer
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (color != Color.Transparent) {
                                    IconButton(
                                        onClick = {
                                            // Ao clicar, a exclusão é chamada, mas o confirmValueChange
                                            // já faria isso se o deslize fosse até o fim.
                                            // Para manter o comportamento de exclusão por swipe completo:
                                            onDeleteNote(individualNoteWithTags.note)
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Excluir nota",
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        },
                        enableDismissFromEndToStart = true,
                        enableDismissFromStartToEnd = false
                    ) {
                        NoteCard(
                            title = individualNoteWithTags.note.title,
                            content = individualNoteWithTags.note.content,
                            date = individualNoteWithTags.note.timeStamp,
                            navController = navController,
                            id = individualNoteWithTags.note.id
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun NoteCard(
    title: String, content: String, date: Instant, navController: NavController? = null, id: Int
) {
    val dateFormat = stringResource(id = R.string.date_format)
    val formattedDate = remember(date, dateFormat) {
        DateTimeFormatter.ofPattern(dateFormat)
            .withZone(ZoneId.systemDefault())
            .format(date)
    }

    Surface(
        tonalElevation = 16.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Box(

            modifier = Modifier
                .clickable(
                    onClick = {
                        navController?.navigate(Screen.Note.withArgs(id.toString()))
                    })


        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp)

            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1

                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd

                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Right
                    )
                }
            }
        }
    }
}

@Composable
fun NoteChips(label: String, isSelected: Boolean, onSelectedChange: () -> Unit) {
    FilterChip(
        onClick = onSelectedChange,
        label = { Text(label) },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        elevation = FilterChipDefaults.filterChipElevation(
            elevation = 2.dp,
            pressedElevation = 4.dp
        ),
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Filtro Ativo",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}

@Preview
@Composable
fun NoteChipsPreview() {
    NoteChips("Teste", true, {})
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun MainViewPreview() {
    GlyphTheme {
        val sampleNotes = (1..15).map {
            NoteEntity(
                id = it,
                title = "Sample Note Title $it",
                content = "This is sample content for note $it",
                timeStamp = Instant.now()
            )
        }
        val sampleTags = listOf(TagEntity(tagId = 1, name = "Life"), TagEntity(tagId = 2, name = "Work"))
        val sampleNotesWithTags = sampleNotes.map {
            NoteWithTags(note = it, tags = if (it.id % 2 == 0) listOf(sampleTags[0]) else listOf(sampleTags[1]))
        }
        MainView(
            notes = sampleNotesWithTags,
            tags = sampleTags,
            onFabClick = {},
            onDeleteNote = {}
        )
    }
}
