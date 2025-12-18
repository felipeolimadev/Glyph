package com.felipeserver.site.glyph.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.felipeserver.site.glyph.model.NoteModel
import com.felipeserver.site.glyph.viewmodel.NoteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
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
                        "Large Top App Barr",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) { }

    }


}

@Composable
@Preview(showBackground = true)
fun MainViewPreview() {
    MainView()


}


@Composable
fun MainTitle() {
    Text(text = "Your Mind")

}

@Composable
fun NoteCard(noteTitle: String, noteContent: String, noteDate: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .fillMaxWidth()
            .height(100.dp)

    ) {
        Text(
            text = noteTitle,
            style = MaterialTheme.typography.titleLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = noteContent,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd

        ) {
            Text(
                text = noteDate,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Right
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NoteCardPreview() {
    NoteCard(
        "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris finibus, odio ac mattis fermentum, elit erat condimentum nibh, vitae suscipit nunc purus ut diam. Vivamus fermentum purus enim, vel molestie justo feugiat et. Nam dignissim nunc vitae tristique posuere. Sed egestas turpis neque. Aenean elit metus, auctor id erat sit amet, feugiat cursus diam. In hac habitasse platea dictumst. Praesent vitae sollicitudin lacus. Sed in magna lobortis, euismod risus sed, luctus lacus. Morbi eu ligula convallis, convallis ex at, rutrum urna. Vivamus felis purus, sollicitudin in ullamcorper in, bibendum vel erat. Duis et quam in nisl tristique lacinia. Vestibulum aliquet tortor ac ex hendrerit vehicula.",
        "25-12-2012"
    )
}
