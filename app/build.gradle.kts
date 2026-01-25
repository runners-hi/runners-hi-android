plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.runnersHi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.runnersHi"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Kakao SDK App Key (local.properties에서 읽어옴)
        val kakaoAppKey = project.findProperty("KAKAO_NATIVE_APP_KEY") as? String ?: ""
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoAppKey\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoAppKey
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Module dependencies
    implementation(project(":core:common"))

    // Domain modules
    implementation(project(":domain:splash:api"))
    implementation(project(":domain:splash:impl"))
    implementation(project(":domain:auth:api"))
    implementation(project(":domain:auth:impl"))
    implementation(project(":domain:user:api"))
    implementation(project(":domain:user:impl"))
    implementation(project(":domain:ranking:api"))
    implementation(project(":domain:ranking:impl"))

    // Data modules
    implementation(project(":data:splash:api"))
    implementation(project(":data:splash:impl"))
    implementation(project(":data:auth:api"))
    implementation(project(":data:auth:impl"))
    implementation(project(":data:user:api"))
    implementation(project(":data:user:impl"))
    implementation(project(":data:ranking:api"))
    implementation(project(":data:ranking:impl"))

    // Presentation modules
    implementation(project(":presentation:common"))
    implementation(project(":presentation:splash"))
    implementation(project(":presentation:login"))
    implementation(project(":presentation:terms"))
    implementation(project(":presentation:main"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    testImplementation(libs.junit)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Kakao SDK
    implementation(libs.kakao.user)

    // Credentials (for Apple Sign In)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
}
