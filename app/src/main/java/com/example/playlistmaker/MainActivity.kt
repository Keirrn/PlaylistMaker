package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.search_btn)
        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        /*
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "test", Toast.LENGTH_SHORT).show()
            }
        }

        btnSearch.setOnClickListener(buttonClickListener)
        */

        val btnMedia = findViewById<Button>(R.id.media_btn)
        btnMedia.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }
        /*
        btnMedia.setOnClickListener {
            Toast.makeText(this@MainActivity, "test test", Toast.LENGTH_SHORT).show()
        }
        */

        val btnSetting = findViewById<Button>(R.id.settings_btn)
        btnSetting.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        /*
        btnSetting.setOnClickListener {
            Toast.makeText(this@MainActivity, "test test te", Toast.LENGTH_SHORT).show()
        }
        */
    }
}