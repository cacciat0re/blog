package cn.arorms.blog.app

import cn.arorms.blog.app.properties.S3Properties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(S3Properties::class)
@EnableScheduling
class AppApplication

fun main(args: Array<String>) {
    runApplication<AppApplication>(*args)
}
