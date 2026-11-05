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
        // ksoap2-android (cliente SOAP para Android) no está en Maven Central
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/ksoap2-android-releases/")
        }
    }
}

rootProject.name = "EurekaBank_CliMov_GR8"
include(":app")
