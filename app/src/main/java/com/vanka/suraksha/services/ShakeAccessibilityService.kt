package com.vanka.suraksha.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class ShakeAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used in this example
    }

    override fun onInterrupt() {
        // Not used in this example
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("ShakeAccessibilityService", "Service connected")
    }

    override fun onGesture(gestureId: Int): Boolean {
        if (gestureId == GESTURE_SWIPE_UP) {
            // Shake gesture detected, open the app in the background
            openAppInBackground()
        }
        return super.onGesture(gestureId)
    }

    private fun openAppInBackground() {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            launchIntent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(it)
            }
        } catch (e: Exception) {
            Log.e("ShakeAccessibilityService", "Failed to open app: ${e.message}")
        }
    }



    override fun onKeyEvent(event: KeyEvent?): Boolean {
        // Not used in this example
        return super.onKeyEvent(event)
    }
}
