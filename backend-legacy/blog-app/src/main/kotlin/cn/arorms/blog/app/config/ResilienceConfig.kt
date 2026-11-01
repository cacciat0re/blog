package cn.arorms.blog.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.resilience.annotation.EnableResilientMethods

@Configuration
@EnableResilientMethods
class ResilienceConfig