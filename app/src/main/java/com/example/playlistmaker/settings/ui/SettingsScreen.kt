package com.example.playlistmaker.settings.ui

import android.content.res.ColorStateList
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val isDarkTheme by viewModel.isDarkTheme.observeAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.backgrounds))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.backgrounds))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.options),
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = colorResource(R.color.text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 8.dp, start = 12.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.is_dark_theme),
                fontSize = 16.sp,
                color = colorResource(id = R.color.text)
            )

            Switch(
                checked = isDarkTheme,
                onCheckedChange = { isChecked ->
                    viewModel.onThemeSwitched(isChecked)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorResource(id = R.color.switch_thumb),
                    checkedTrackColor = colorResource(id = R.color.switch_track),
                )
            )
        }

        SettingsMenuItem(
            textResId = R.string.share,
            iconResId = R.drawable.share_ic,
            onClick = { viewModel.onShareClicked() }
        )
        SettingsMenuItem(
            textResId = R.string.message_sup,
            iconResId = R.drawable.support_ic,
            onClick = { viewModel.onSupportClicked() }
        )
        SettingsMenuItem(
            textResId = R.string.agreement,
            iconResId = R.drawable.agreement_ic,
            onClick = { viewModel.onAgreementClicked() }
        )
    }
}

@Composable
fun SettingsMenuItem(textResId: Int, iconResId: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 21.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = textResId),
            fontSize = 14.sp,
            color = colorResource(id = R.color.text)
        )
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}