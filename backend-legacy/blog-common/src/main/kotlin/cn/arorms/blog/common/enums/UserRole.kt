package cn.arorms.blog.common.enums

/**
 * User roles for authorization
 */
enum class UserRole {
    ADMIN,      // Full access to all features
    PUBLISHER,  // Can create and edit articles
    USER,       // Standard user with basic access
    GUEST       // Read-only access
}
