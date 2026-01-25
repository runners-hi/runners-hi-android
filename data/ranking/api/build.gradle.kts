plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:ranking:api"))
    implementation(project(":domain:user:api"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
