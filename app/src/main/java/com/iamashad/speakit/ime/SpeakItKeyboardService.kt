package com.iamashad.speakit.ime

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.airbnb.lottie.LottieAnimationView
import com.iamashad.speakit.userinterface.keyboard.KeyboardController
import com.iamashad.speakit.userinterface.keyboard.KeyboardUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SpeakItKeyboardService : InputMethodService() {

    @Inject
    lateinit var controller: KeyboardController

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(
            resources.getIdentifier("keyboard_view", "layout", packageName),
            null
        ) as LinearLayout

        val mic = root.findViewById<LottieAnimationView>(
            resources.getIdentifier("micButton", "id", packageName)
        )
        val status = root.findViewById<TextView>(
            resources.getIdentifier("statusText", "id", packageName)
        )

        scope.launch {
            controller.state.collectLatest { s ->
                when (s) {
                    KeyboardUiState.Idle -> {
                        mic.speed = 1.0f
                        mic.pauseAnimation()
                        mic.isEnabled = true
                        status.text = "Hold to Speak"
                    }

                    KeyboardUiState.Recording -> {
                        mic.speed = 1.0f
                        if (!mic.isAnimating) mic.playAnimation()
                        status.text = "Recording… release to transcribe"
                    }

                    KeyboardUiState.Processing -> {
                        mic.isEnabled = false
                        mic.speed = 1.5f
                        if (!mic.isAnimating) mic.playAnimation()
                        status.text = "Transcribing…"
                    }

                    is KeyboardUiState.Inserted -> {
                        mic.pauseAnimation()
                        mic.isEnabled = true
                        status.text = "Inserted"
                        Toast.makeText(
                            this@SpeakItKeyboardService,
                            "Text inserted ✓",
                            Toast.LENGTH_SHORT
                        ).show()
                        root.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                    }

                    is KeyboardUiState.Error -> {
                        mic.pauseAnimation()
                        mic.isEnabled = true
                        status.text = s.message
                        Toast.makeText(this@SpeakItKeyboardService, s.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        mic.setOnTouchListener { v, ev ->
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (!hasMicPermission()) {
                        Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT)
                            .show()
                        openAppSettings()
                        return@setOnTouchListener true
                    }
                    controller.onPressDown()
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    v.performClick()
                    controller.onRelease { insertText(it) }
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    controller.onCancel()
                    true
                }

                else -> false
            }
        }
        mic.setOnClickListener {

        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun insertText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    private fun hasMicPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun openAppSettings() {
        val uri = "package:$packageName".toUri()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}
