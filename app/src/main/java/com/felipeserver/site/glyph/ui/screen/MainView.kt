package com.felipeserver.site.glyph.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.felipeserver.site.glyph.data.local.NoteEntity
import com.felipeserver.site.glyph.navigation.Screen
import com.felipeserver.site.glyph.ui.viewmodel.NoteViewModel
import kotlin.collections.emptyList


// Wrapper composable that uses the ViewModel - use this in your Activity/NavGraph
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainViewScreen(navController: NavController, viewModel: NoteViewModel = viewModel()) {
    val notes by viewModel.notes.collectAsState()
    MainView(notes = notes, navController = navController)
}

// Pure UI composable that can be previewed
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
                ),
                title = {
                    Text(
                        "Glyph",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.displayLargeEmphasized
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
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
fun NoteCard(title: String, content: String, date: String, navController: NavController? = null,id: Int) {

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {                        
                        navController?.navigate(Screen.Note.withArgs(id.toString()))
                    }
                )
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
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd

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
