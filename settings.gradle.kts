@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = java.net.URI("https://s3.amazonaws.com/repo.commonsware.com")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url  = java.net.URI("https://s3.amazonaws.com/repo.commonsware.com")
        }
    }
}

rootProject.name = "GoodsApp"
include(":app")
 