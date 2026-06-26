package com.example.playlistmaker.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.Track

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    favoritesState: FavoritesState,
    playlists: List<Playlist>,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    imageLoader: ImageLoadRepository,
    modifier: Modifier = Modifier
) {

    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.backgrounds))
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.media),
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 8.dp, start = 12.dp)
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = colorResource(R.color.backgrounds),
            contentColor = colorResource(R.color.text),
            divider = {},
            indicator = { positions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(positions[selectedTab]),
                    color = colorResource(R.color.text)
                )
            },
        ) {

            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(stringResource(R.string.favorites_track))
                }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(stringResource(R.string.Playlists))
                }
            )
        }

        when (selectedTab) {

            0 -> FavoritesContent(
                state = favoritesState,
                onTrackClick = onTrackClick,
            )

            1 -> PlaylistsContent(
                playlists = playlists,
                onPlaylistClick = onPlaylistClick,
                onCreatePlaylistClick = onCreatePlaylistClick,
                imageLoader = imageLoader
            )
        }
    }
}