pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}

dependencyResolutionManagement {
    repositories {
        // Common artifact
        maven("https://gitlab.arorms.cn/api/v4/projects/14/packages/maven")
        // Security artifact
        maven("https://gitlab.arorms.cn/api/v4/projects/17/packages/maven")
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

rootProject.name = "backend"
include("blog-common", "blog-app")