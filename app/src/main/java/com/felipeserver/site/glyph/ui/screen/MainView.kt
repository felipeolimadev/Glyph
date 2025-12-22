package com.felipeserver.site.glyph.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.felipeserver.site.glyph.data.local.NoteEntity
import com.felipeserver.site.glyph.navigation.Screen
import com.felipeserver.site.glyph.ui.viewmodel.NoteViewModel
import kotlin.collections.emptyList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainViewScreen(navController: NavController, viewModel: NoteViewModel = viewModel()) {
    val notes by viewModel.notes.collectAsState()
    MainView(notes = notes, navController = navController)
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainView(notes: List<NoteEntity> = emptyList(), navController: NavController? = null) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ), title = {
                    Text(
                        "Glyph",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.displayLargeEmphasized
                    )
                }, scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar(

            ) { }
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(notes) { individualNote ->
                NoteCard(
                    title = individualNote.title,
                    content = individualNote.content,
                    date = individualNote.timeStamp.toString(),
                    navController = navController,
                    id = individualNote.id
                )
            }
        }
    }
}

@Composable
fun NoteCard(
    title: String, content: String, date: String, navController: NavController? = null, id: Int
) {

    Box(
        modifier = Modifier.clickable(
            onClick = {
                navController?.navigate(Screen.Note.withArgs(id.toString()))
            })) {
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
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd

            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(viewModel: NoteViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val notes by viewModel.notes.collectAsState()

    var active by remember { mutableStateOf(false) }
    val filteredItems = notes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
    }
    SearchBar(
        query = searchQuery,
        onQueryChange = {
            searchQuery = it
        },
        onSearch = {
            active = false
        },
        active = active,
        onActiveChange = {
            active = it
        },
        modifier = Modifier
            .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
            .fillMaxWidth(),

        placeholder = { Text("Search") },

        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (active)
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        tonalElevation = 0.dp,
    ) {

        //Search content here

        filteredItems.forEach{ item->
            Text(text = item.title, fontSize = 12.sp)
        }
    }

}


@Composable
@Preview(showBackground = true)
fun MainViewPreview() {
    val sampleNotes = (1..15).map {
        NoteEntity(
            id = it,
            title = "Sample Note Title $it",
            content = "This is a sample note content for preview purposes.",
            timeStamp = System.currentTimeMillis()
        )
    }
    MainView(notes = sampleNotes)


}

@Composable
@Preview(showBackground = true)
fun NoteCardPreview() {
    NoteCard(
        "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris finibus, odio ac mattis fermentum, elit erat condimentum nibh, vitae suscipit nunc purus ut diam. Vivamus fermentum purus enim, vel molestie justo feugiat et. Nam dignissim nunc vitae tristique posuere. Sed egestas turpis neque. Aenean elit metus, auctor id erat sit amet, feugiat cursus diam. In hac habitasse platea dictumst. Praesent vitae sollicitudin lacus. Sed in magna lobortis, euismod risus sed, luctus lacus. Morbi eu ligula convallis, convallis ex at, rutrum urna. Vivamus felis purus, sollicitudin in ullamcorper in, bibendum vel erat. Duis et quam in nisl tristique lacinia. Vestibulum aliquet tortor ac ex hendrerit vehicula.",
        "25-12-2012",
        id = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun SearchBarPreview() {
    val sampleNotes = (1..5).map {
        NoteEntity(
            id = it,
            title = "Sample Note Title $it",
            content = "This is a sample note content for preview purposes.",
            timeStamp = System.currentTimeMillis()
        )
    }
    var searchQuery by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val filteredItems = sampleNotes.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
    }

    SearchBar(
        query = searchQuery,
        onQueryChange = {
            searchQuery = it
        },
        onSearch = {
            active = false
        },
        active = active,
        onActiveChange = {
            active = it
        },
        modifier = Modifier
            .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
            .fillMaxWidth(),

        placeholder = { Text("Search") },

        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (active)
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        tonalElevation = 0.dp,
    ) {

        //Search content here
        filteredItems.forEach { item ->
            Text(text = item.title, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
        }
    }
}
