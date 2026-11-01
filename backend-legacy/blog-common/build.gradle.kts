plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    api("cn.arorms.framework:arorms-common:1.1.0-SNAPSHOT")
//    implementation("org.redisson:redisson-spring-boot-starter:3.45.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
