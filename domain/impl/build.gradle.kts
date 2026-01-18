plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:api"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
