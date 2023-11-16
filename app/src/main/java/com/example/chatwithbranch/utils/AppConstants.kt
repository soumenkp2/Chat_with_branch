package com.example.chatwithbranch.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class AppConstants {
    companion object {
        const val BASE_URL = "https://android-messaging.branch.co/"
        const val SHARED_PREF_NAME = "sp_branch"
        const val TOKEN = "token"

        fun snackBarTemplate(view: View, text: String): Snackbar {
            return Snackbar.make(view,text, Snackbar.LENGTH_INDEFINITE)
        }
    }
}