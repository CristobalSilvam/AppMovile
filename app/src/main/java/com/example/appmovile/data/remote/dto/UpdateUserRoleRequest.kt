package com.example.appmovile.data.remote.dto

data class UpdateUserRoleRequest(
    val userId: Long,
    val newRole: String
)