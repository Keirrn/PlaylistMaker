package com.example.playlistmaker.media.ui

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.Track

@Composable
fun PlaylistsContent(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    imageLoader: ImageLoadRepository
) {

    Column {

        Button(
            onClick = onCreatePlaylistClick,
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.text),
                contentColor = colorResource(R.color.backgrounds)
            ),
            shape = RoundedCornerShape(54.dp)
        ){

            Text(stringResource(R.string.create_playlist))
        }

        if (playlists.isEmpty()) {

            Placeholder(
                image = R.drawable.nofound,
                text = stringResource(R.string.users_playlists_is_empty)
            )

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {

                items(
                    items = playlists,
                    key = { it.playlistId }
                ) { playlist ->

                    PlaylistItem(
                        playlist = playlist,
                        imageLoader = imageLoader,
                        onClick = {
                            onPlaylistClick(playlist)
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun Placeholder(
    @DrawableRes image: Int,
    text: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(140.dp))

        Image(
            painter = painterResource(image),
            contentDescription = null
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = text,
            color = colorResource(R.color.text),
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            fontWeight = FontWeight.Medium,
            fontSize = dimensionResource(R.dimen.text_title).value.sp,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun PlaylistItem(
    playlist: Playlist,
    imageLoader: ImageLoadRepository,
    onClick: () -> Unit
){

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            },
            update = { imageView ->
                if (playlist.coverPath.isNullOrEmpty()) {
                    imageView.setImageResource(R.drawable.placeholder)
                } else {
                    imageLoader.loadImage(
                        playlist.coverPath,
                        imageView,
                        8f
                    )
                }
            }
        )

        Text(
            text = playlist.playlistName,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small)),
            color = colorResource(R.color.text),
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontSize = dimensionResource(R.dimen.text_mini).value.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${playlist.tracksCount} треков",
            color = colorResource(R.color.text),
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontWeight = FontWeight.Normal,
            fontSize = dimensionResource(R.dimen.text_mini).value.sp
        )
    }
}
