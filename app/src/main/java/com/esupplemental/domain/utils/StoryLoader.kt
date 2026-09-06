package com.esupplemental.domain.utils

import android.content.Context

object StoryLoader {

    fun load(context: Context, assetPath: String): String {
        return context.assets
            .open(assetPath)
            .bufferedReader()
            .use { it.readText() }
    }
}