package com.esupplemental.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)