package com.example.playlistmaker.media.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TrackItem

@Composable
fun FavoritesContent(
    state: FavoritesState,
    onTrackClick: (Track) -> Unit
) {

    when (state) {

        FavoritesState.Empty -> {

            Placeholder(
                image = R.drawable.nofound,
                text = stringResource(R.string.favorites_is_empty)
            )
        }

        is FavoritesState.Content -> {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp
                )
            ) {

                items(state.tracks) { track ->

                    TrackItem(
                        track = track,
                        onClick = onTrackClick
                    )
                }
            }
        }
    }
}