plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.spring") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.jpa") version "2.2.21" apply false
    id("org.jetbrains.kotlin.kapt") version "2.2.21" apply false
    id("org.springframework.boot") version "4.0.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "cn.arorms.blog"
version = "1.2.4"

subprojects {
    group = rootProject.group
    version = rootProject.version
}
