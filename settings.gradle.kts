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
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
    }
}

rootProject.name = "RunnersHi"

include(":app")
include(":core:common")
include(":core:network")

// Domain modules
include(":domain:splash:api")
include(":domain:splash:impl")
include(":domain:auth:api")
include(":domain:auth:impl")
include(":domain:user:api")
include(":domain:user:impl")
include(":domain:ranking:api")
include(":domain:ranking:impl")
include(":domain:terms:api")
include(":domain:terms:impl")
include(":domain:region:api")
include(":domain:region:impl")
include(":domain:home:api")
include(":domain:home:impl")
include(":domain:health:api")
include(":domain:health:impl")
include(":domain:mission:api")
include(":domain:mission:impl")
include(":domain:notice:api")
include(":domain:notice:impl")
include(":domain:notification:api")
include(":domain:notification:impl")

// Data modules
include(":data:splash:api")
include(":data:splash:impl")
include(":data:auth:api")
include(":data:auth:impl")
include(":data:user:api")
include(":data:user:impl")
include(":data:ranking:api")
include(":data:ranking:impl")
include(":data:terms:api")
include(":data:terms:impl")
include(":data:region:api")
include(":data:region:impl")
include(":data:home:api")
include(":data:home:impl")
include(":data:health:api")
include(":data:health:impl")
include(":data:mission:api")
include(":data:mission:impl")
include(":data:notice:api")
include(":data:notice:impl")
include(":data:notification:api")
include(":data:notification:impl")

// Presentation modules
include(":presentation:splash:api")
include(":presentation:splash:impl")
include(":presentation:login:api")
include(":presentation:login:impl")
include(":presentation:launcher:api")
include(":presentation:launcher:impl")
include(":presentation:terms")
include(":presentation:region")
include(":presentation:main")
include(":presentation:ranking")
include(":presentation:record")
include(":presentation:mission")
include(":presentation:notice")
include(":presentation:notification")
include(":presentation:mypage")
include(":presentation:common")
