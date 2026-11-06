import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

// 실제 Firebase 설정 파일이 있을 때만 켠다. 없으면 푸시 없이 빌드된다.
val firebaseConfig = file("google-services.json")
if (firebaseConfig.exists() && !firebaseConfig.readText().contains("placeholder")) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    namespace = "com.runnersHi"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.runnersHi"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        // Kakao SDK App Key: local.properties → gradle property 순으로 읽음 (없으면 Mock 로그인)
        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) file.inputStream().use { load(it) }
        }
        val kakaoAppKey = localProperties.getProperty("KAKAO_NATIVE_APP_KEY")
            ?: project.findProperty("KAKAO_NATIVE_APP_KEY") as? String
            ?: ""
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoAppKey\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoAppKey

        // 값은 저장소 밖(-P 옵션 또는 local.properties)에 둔다. 서버 주소가 없으면 목업으로 동작한다.
        fun config(name: String, default: String = ""): String =
            project.findProperty(name) as? String ?: localProperties.getProperty(name) ?: default
        val apiBaseUrl = config("API_BASE_URL")
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "APPLE_SERVICE_ID", "\"${config("APPLE_SERVICE_ID")}\"")
        buildConfigField("String", "APPLE_REDIRECT_URI", "\"${config("APPLE_REDIRECT_URI")}\"")
        buildConfigField(
            "String",
            "APPLE_AUTHORIZE_URL",
            "\"${config("APPLE_AUTHORIZE_URL", "https://appleid.apple.com/auth/authorize")}\""
        )
        // 로컬 서버(http)로 붙을 때만 평문 통신을 허용한다.
        manifestPlaceholders["usesCleartextTraffic"] = apiBaseUrl.startsWith("http://").toString()
    }

    testOptions {
        // 테스트마다 새 프로세스와 빈 앱 데이터로 시작한다.
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    buildFeatures {
        buildConfig = true
    }

    // 서명 값은 저장소 밖(keystore.properties)에 둔다. 파일이 없으면 서명 없이 빌드된다.
    val keystoreFile = rootProject.file("keystore.properties")
    val releaseSigning = if (keystoreFile.exists()) {
        val keystore = Properties().apply { keystoreFile.inputStream().use { load(it) } }
        signingConfigs.create("release") {
            storeFile = rootProject.file(keystore.getProperty("storeFile"))
            storePassword = keystore.getProperty("storePassword")
            keyAlias = keystore.getProperty("keyAlias")
            keyPassword = keystore.getProperty("keyPassword")
        }
    } else {
        null
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = releaseSigning
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    // Module dependencies
    implementation(project(":core:common"))
    implementation(project(":core:network"))

    // Domain modules
    implementation(project(":domain:splash:api"))
    implementation(project(":domain:splash:impl"))
    implementation(project(":domain:auth:api"))
    implementation(project(":domain:auth:impl"))
    implementation(project(":domain:user:api"))
    implementation(project(":domain:user:impl"))
    implementation(project(":domain:ranking:api"))
    implementation(project(":domain:ranking:impl"))
    implementation(project(":domain:terms:api"))
    implementation(project(":domain:terms:impl"))
    implementation(project(":domain:region:api"))
    implementation(project(":domain:region:impl"))
    implementation(project(":domain:home:api"))
    implementation(project(":domain:home:impl"))
    implementation(project(":domain:health:api"))
    implementation(project(":domain:health:impl"))
    implementation(project(":domain:mission:api"))
    implementation(project(":domain:mission:impl"))
    implementation(project(":domain:notice:api"))
    implementation(project(":domain:notice:impl"))
    implementation(project(":domain:notification:api"))
    implementation(project(":domain:notification:impl"))

    // Data modules
    implementation(project(":data:splash:api"))
    implementation(project(":data:splash:impl"))
    implementation(project(":data:auth:api"))
    implementation(project(":data:auth:impl"))
    implementation(project(":data:user:api"))
    implementation(project(":data:user:impl"))
    implementation(project(":data:ranking:api"))
    implementation(project(":data:ranking:impl"))
    implementation(project(":data:terms:api"))
    implementation(project(":data:terms:impl"))
    implementation(project(":data:region:api"))
    implementation(project(":data:region:impl"))
    implementation(project(":data:home:api"))
    implementation(project(":data:home:impl"))
    implementation(project(":data:health:api"))
    implementation(project(":data:health:impl"))
    implementation(project(":data:mission:api"))
    implementation(project(":data:mission:impl"))
    implementation(project(":data:notice:api"))
    implementation(project(":data:notice:impl"))
    implementation(project(":data:notification:api"))
    implementation(project(":data:notification:impl"))

    // Presentation modules
    implementation(project(":presentation:common"))
    implementation(project(":presentation:launcher:api"))
    implementation(project(":presentation:launcher:impl"))
    implementation(project(":presentation:splash:api"))
    implementation(project(":presentation:splash:impl"))
    implementation(project(":presentation:login:api"))
    implementation(project(":presentation:login:impl"))
    implementation(project(":presentation:terms"))
    implementation(project(":presentation:region"))
    implementation(project(":presentation:main"))
    implementation(project(":presentation:ranking"))
    implementation(project(":presentation:record"))
    implementation(project(":presentation:mission"))
    implementation(project(":presentation:notice"))
    implementation(project(":presentation:notification"))
    implementation(project(":presentation:mypage"))

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
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestUtil(libs.androidx.test.orchestrator)
    androidTestUtil(libs.androidx.test.services)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
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

    // Health Connect
    implementation(libs.health.connect)
}

// 목업으로 출시되는 일을 막는다. 스토어용 번들은 실서버 주소와 카카오 키가 있어야 만들어진다.
tasks.matching { it.name == "bundleRelease" }.configureEach {
    val releaseFields = android.defaultConfig.buildConfigFields
    val baseUrl = releaseFields["API_BASE_URL"]?.value.orEmpty().trim('"')
    val kakaoKey = releaseFields["KAKAO_NATIVE_APP_KEY"]?.value.orEmpty().trim('"')
    doFirst {
        check(baseUrl.startsWith("https://")) { "출시 빌드에는 https API_BASE_URL이 필요합니다" }
        check(kakaoKey.isNotBlank()) { "출시 빌드에는 KAKAO_NATIVE_APP_KEY가 필요합니다" }
    }
}
