package com.example.playlistmaker.search.ui

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.room.util.wrapMappedColumns
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track

@Composable
fun SearchScreen(viewModel: SearchViewModel) {

    val state by viewModel.searchState.observeAsState(SearchState.Empty)
    val history by viewModel.historyState.observeAsState(emptyList())
    val clearVisible by viewModel.clearButtonVisible.observeAsState(false)
    val query by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.backgrounds))
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = stringResource(R.string.search),
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 8.dp, start = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(
                    color = colorResource(R.color.searching_filed),
                    shape = RoundedCornerShape(8.dp)
                )
                .height(36.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(R.drawable.search_icon),
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                BasicTextField(
                    value = query,
                    onValueChange = {
                        viewModel.onTextChanged(it)
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                        color = colorResource(R.color.black)
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search),
                                fontSize = 16.sp,
                                color = colorResource(R.color.items),
                                fontFamily = FontFamily(Font(R.font.ys_display_regular))
                            )
                        }
                        inner()
                    }
                )

                if (clearVisible) {
                    Image(
                        painter = painterResource(R.drawable.clear_ic),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            viewModel.clearSearch()
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (state is SearchState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = colorResource(R.color.blue),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 140.dp)
                )
            }
            return
        }

        if ((state is SearchState.History || (state is SearchState.Empty && history.isNotEmpty())) && query.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Text(
                    text = stringResource(R.string.you_search),
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontSize = 19.sp,
                    color = colorResource(R.color.text),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp, bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(history) { track ->
                        TrackItem(track, viewModel::onTrackClicked)
                    }
                }

                Button(
                    onClick = { viewModel.clearHistory() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp, bottom = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.text),
                        contentColor = colorResource(R.color.backgrounds)
                    ),
                    shape = RoundedCornerShape(54.dp)
                ) {
                    Text(
                        text = stringResource(R.string.remove_history),
                        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        fontSize = 14.sp
                    )
                }
            }

            return
        }

        if (state is SearchState.Content) {

            val tracks = (state as SearchState.Content).tracks

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = viewModel::onTrackClicked,
                    )
                }
            }

            return
        }

        if (state is SearchState.Error) {

            val error = state as SearchState.Error

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(102.dp))

                Image(
                    painter = painterResource(
                        if (error.isNetworkError) R.drawable.nointernet
                        else R.drawable.nofound
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = error.errorMessage,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    fontSize = 19.sp,
                    color = colorResource(R.color.text),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                if (error.isNetworkError) {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.refreshSearch() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.text),
                            contentColor = colorResource(R.color.backgrounds)
                        ),
                        shape = RoundedCornerShape(54.dp)
                    ) {
                        Text(
                            text = "Обновить",
                            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    onClick: (Track) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .clickable { onClick(track) }
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {

            Text(
                text = track.trackName,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = colorResource(R.color.text),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = track.artistName,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(R.color.singer),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = " • ${track.trackTime}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(R.color.singer),
                    maxLines = 1
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.agreement_ic),
            contentDescription = null,
            modifier = Modifier.padding(end = 13.dp)
        )
    }
}