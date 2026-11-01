plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.plugin.jpa")
    id("org.jetbrains.kotlin.kapt")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property"
        )
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":blog-common"))

    // ===== Spring Boot =====
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // ===== Spring AI =====
    implementation("org.springframework.ai:spring-ai-starter-model-openai")

    // ===== Kotlin / Jackson =====
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.core:jackson-databind")
    implementation("tools.jackson.module:jackson-module-kotlin")

    // ===== QueryDSL =====
    implementation("com.querydsl:querydsl-jpa:5.1.0") { artifact { classifier = "jakarta" } }
    kapt("com.querydsl:querydsl-apt:5.1.0") { artifact { classifier = "jakarta" } }

    // ===== Database =====
    runtimeOnly("org.postgresql:postgresql")

    // ===== Image =====
    implementation("net.coobird:thumbnailator:0.4.20")

    // ===== Framework =====
    implementation("cn.arorms.framework:arorms-security:1.0.1")

    implementation("io.github.resilience4j:resilience4j-spring-boot4:2.4.0")

    // ===== AWS =====
    implementation("software.amazon.awssdk:s3:2.31.0")

    // ===== Test =====
    testImplementation("org.springframework.graphql:spring-graphql-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:2.0.1")
    }
}

// Specific the compile file
tasks.bootJar {
    archiveFileName.set("backend.jar")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
