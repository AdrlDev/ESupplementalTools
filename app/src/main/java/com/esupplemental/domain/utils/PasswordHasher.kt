package com.esupplemental.domain.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PasswordHasher {
    companion object {
        private const val ITERATIONS = 65536
        private const val KEY_LENGTH = 256
        private const val ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val SALT_LENGTH = 16

        fun hash(password: String): String {
            val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
            val hash = pbkdf2(password, salt)
            val saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP)
            val hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP)
            return "$saltB64:$hashB64"
        }

        fun verify(password: String, stored: String): Boolean {
            return try {
                val parts = stored.split(":")
                if (parts.size != 2) return false
                val salt = Base64.decode(parts[0], Base64.NO_WRAP)
                val storedHash = Base64.decode(parts[1], Base64.NO_WRAP)
                val inputHash = pbkdf2(password, salt)
                inputHash.size == storedHash.size &&
                        inputHash.zip(storedHash).all { (a, b) -> a == b }
            } catch (_: Exception) {
                false
            }
        }

        private fun pbkdf2(password: String, salt: ByteArray): ByteArray {
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
            val factory = SecretKeyFactory.getInstance(ALGORITHM)
            return factory.generateSecret(spec).encoded
        }
    }
}