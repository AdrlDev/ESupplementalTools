package com.esupplemental.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.esupplemental.domain.usecases.media.SyncInitialMediaUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Worker that handles background audio pre-fetching.
 * This continues to run even if the app is closed.
 */
class AudioPrefetchWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val syncInitialMediaUseCase: SyncInitialMediaUseCase by inject()

    override suspend fun doWork(): Result {
        return try {
            syncInitialMediaUseCase.prefetchAllStoryAudio()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // If the error is 401 UNAUTHORIZED or 403 FORBIDDEN, do NOT retry.
            // This is likely a permanent permission or credit issue.
            val message = e.message ?: ""
            if (message.contains("401") || message.contains("403") || message.contains("unauthorized")) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }

    companion object {
        // Bump when a release adds new prefetched content so existing installs
        // enqueue the new one-time queue. Stable media IDs still prevent repeat API use.
        private const val CONTENT_VERSION = 2
        const val WORK_NAME = "AudioPrefetchWork_v$CONTENT_VERSION"
    }
}
