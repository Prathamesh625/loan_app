package com.blocker.accessiblity

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
import android.widget.Toast

class MyAccessibilityService : AccessibilityService() {

    private val TAG = "MyAccessibilityService"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        
        Log.d(TAG, "Accessibility Event received: ${event?.toString()}")

        val packageName = event?.packageName
        val content = event?.contentDescription

        if (packageName != null) {
            Log.d(TAG, "Package Name: $packageName")
        }
        if (content != null) {
            Log.d(TAG, "Content Description: $content")
        }

        if (packageName != null && event?.text != null) {

            if (packageName.toString() == "com.android.settings") {
                if (event.text.toString().contains("Display over other apps")) {
                    Log.i(TAG, "User is in 'Display over other apps' settings")

                    Toast.makeText(
                        this,
                        "Warning: User is modifying overlay permissions!",
                        Toast.LENGTH_SHORT
                    ).show()

                    performGlobalAction(GLOBAL_ACTION_BACK)

                }
            }
        }

         if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            val rootNode = rootInActiveWindow ?: return

            // Get all visible text from screen
            val allTexts = getAllTextFromNode(rootNode)

            Log.d("Accessibility", "Screen Text: $allTexts")

            // Check if "App Info" related words are present
            Log.d("Accessibility", "Current package: $packageName, Text: $allTexts")

            // Only restrict actions inside the Settings app
            if (allTexts.contains("App info", true) && allTexts.contains("blocker", true)) {
              

                    Log.d("Accessibility", "Blocking 'App Info' access...")
                    performGlobalAction(GLOBAL_ACTION_BACK)
              
            }

            if (allTexts.contains("Accessibility menu", true) && allTexts.contains("Accessibility button", true)) {
              
                    Log.d("Accessibility", "Blocking 'App Info' access...")
                    performGlobalAction(GLOBAL_ACTION_BACK)
              
            }

            if (allTexts.contains("More downloaded services", true) && allTexts.contains("blocker", true) && allTexts.contains("Provided by blocker",true)) {
              
                    Log.d("Accessibility", "Blocking 'Accessiblity' access...")
                    performGlobalAction(GLOBAL_ACTION_BACK)
              
            }

             if (allTexts.contains("blocker is displaying over other apps", true)) {
              
                    Log.d("Accessibility", "Blocking 'Notification Panel' access...")
                    performGlobalAction(GLOBAL_ACTION_BACK)
              
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "Accessibility Service interrupted")
    }

     private fun getAllTextFromNode(node: AccessibilityNodeInfo?): String {
        if (node == null) return ""
        val sb = StringBuilder()

        if (node.text != null) {
            sb.append(node.text.toString()).append("\n")
        }

        for (i in 0 until node.childCount) {
            sb.append(getAllTextFromNode(node.getChild(i)))
        }

        return sb.toString()
    }

    private fun blockAppInfoScreen() {
        Log.d("Accessibility", "Blocking App Info Screen...")

        // Redirect to home screen
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
