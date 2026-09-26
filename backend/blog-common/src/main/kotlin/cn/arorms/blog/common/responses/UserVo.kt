package cn.arorms.blog.common.responses

import cn.arorms.blog.common.enums.UserRole
import java.time.Instant

/**
 * Data Transfer Object for User
 */
data class UserVo(
    val id: Long?,
    val username: String,
    val email: String,
    val displayName: String?,
    val bio: String?,
    val avatar: String?,
    val role: UserRole,
    val isEnabled: Boolean,
    val createdAt: Instant,
)
