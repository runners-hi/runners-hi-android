pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "RunnersHi"

include(":app")
include(":core:common")

// Domain modules
include(":domain:splash:api")
include(":domain:splash:impl")
include(":domain:auth:api")
include(":domain:auth:impl")
include(":domain:user:api")
include(":domain:user:impl")
include(":domain:ranking:api")
include(":domain:ranking:impl")

// Data modules
include(":data:splash:api")
include(":data:splash:impl")
include(":data:auth:api")
include(":data:auth:impl")
include(":data:user:api")
include(":data:user:impl")
include(":data:ranking:api")
include(":data:ranking:impl")

// Presentation modules
include(":presentation:splash")
include(":presentation:login")
include(":presentation:home")
include(":presentation:common")
