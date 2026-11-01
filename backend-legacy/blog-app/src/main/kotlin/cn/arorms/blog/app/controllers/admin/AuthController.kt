package cn.arorms.blog.app.controllers.admin

import cn.arorms.framework.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author cacc
 * @version 0.10.0 2026-08-19
 * @since 2026-07-29
 */
@RestController
@RequestMapping("/api/admin/auth")
class AuthController {
    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<UserPrincipal> {
        return ResponseEntity.ok(userPrincipal)
    }
}