package com.esupplemental.domain.utils

import java.security.MessageDigest

/** Creates stable cache IDs that change whenever their spoken text changes. */
object AudioCacheKey {
    private const val HASH_BYTES = 6
    private const val HEX_DIGITS = "0123456789abcdef"

    fun fromText(prefix: String, text: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(text.trim().toByteArray(Charsets.UTF_8))
        val shortHash = buildString(HASH_BYTES * 2) {
            digest.take(HASH_BYTES).forEach { byte ->
                val value = byte.toInt() and 0xff
                append(HEX_DIGITS[value ushr 4])
                append(HEX_DIGITS[value and 0x0f])
            }
        }
        return "${prefix}_$shortHash"
    }
}
