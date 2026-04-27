package com.screenreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.nl.translate.TranslateLanguage

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var languageSpinner: Spinner
    
    companion object {
        const val OVERLAY_PERMISSION_REQUEST_CODE = 1234
        var selectedTargetLanguage = TranslateLanguage.ARABIC
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        languageSpinner = findViewById(R.id.languageSpinner)

        setupLanguageSpinner()
        setupButtons()
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf(
            getString(R.string.arabic),
            getString(R.string.english),
            getString(R.string.turkish),
            getString(R.string.persian)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTargetLanguage = when (position) {
                    0 -> TranslateLanguage.ARABIC
                    1 -> TranslateLanguage.ENGLISH
                    2 -> TranslateLanguage.TURKISH
                    3 -> TranslateLanguage.PERSIAN
                    else -> TranslateLanguage.ARABIC
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        startButton.setOnClickListener {
            if (checkOverlayPermission()) {
                startFloatingService()
            } else {
                requestOverlayPermission()
            }
        }

        stopButton.setOnClickListener {
            stopFloatingService()
        }
    }

    private fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (checkOverlayPermission()) {
                startFloatingService()
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startFloatingService() {
        val intent = Intent(this, FloatingButtonService::class.java)
        startService(intent)
        startButton.isEnabled = false
        stopButton.isEnabled = true
        Toast.makeText(this, R.string.start_service, Toast.LENGTH_SHORT).show()
    }

    private fun stopFloatingService() {
        val intent = Intent(this, FloatingButtonService::class.java)
        stopService(intent)
        startButton.isEnabled = true
        stopButton.isEnabled = false
        Toast.makeText(this, R.string.stop_service, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Check if service is running and update button states accordingly
        val isServiceRunning = FloatingButtonService.isServiceRunning
        startButton.isEnabled = !isServiceRunning
        stopButton.isEnabled = isServiceRunning
    }
}
