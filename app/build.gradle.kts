import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services") version "4.4.2"
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")

    id("kotlin-parcelize")
}

android {
    namespace = "com.self.lovenotes"
    compileSdk = 35

    val properties = Properties()
    properties.load(FileInputStream(rootProject.file("local.properties")))

    defaultConfig {
        applicationId = "com.self.lovenotes"
        minSdk = 32
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // BuildConfig에 API Key 추가
        buildConfigField("String", "GEMINI_API_KEY", properties.getProperty("GEMINI_API_KEY"))
        buildConfigField("String", "GOOGLE_AUTH_CLIENT_ID", properties.getProperty("GOOGLE_AUTH_CLIENT_ID"))
        manifestPlaceholders["GOOGLE_MAP_API_KEY"] = properties.getProperty("GOOGLE_MAP_API_KEY")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.googleid)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // 최신 버전 권장

    // navigation
    val nav_version = "2.8.9"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // 버전 수정

    // hilt
    val hilt_version = "2.51.1"
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-android-compiler:$hilt_version")

    // Pager
    implementation("com.google.accompanist:accompanist-pager:0.34.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-firestore")            // firestore - 문서저장소
    implementation("com.google.firebase:firebase-crashlytics")          // crashlytics - 앱 관리 Dashboard
    implementation("com.google.firebase:firebase-analytics")            // analytic - 앱 관리 Dashboard (애널리틱스 기능)
    implementation("com.google.firebase:firebase-perf")                 // performance
    implementation("com.google.firebase:firebase-dynamic-links")        // dynamiclink

    // Coil 이미지 로드
    implementation("io.coil-kt:coil-compose:2.4.0")                     // Coil

    // Credential Manager (통합 로그인인증)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0") // ViewModel용

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // DatePlanner 관련
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.github.jeziellago:compose-markdown:0.5.7")

    // Icon
    implementation("com.woowla.compose.icon.collections:tabler:3.31.0")

    // Map Api
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0") // 위치 선택 시 유용

    implementation("com.google.maps.android:maps-compose:4.3.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.activity:activity-compose:1.8.2")

    // for Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    implementation ("androidx.exifinterface:exifinterface:1.3.7")

    implementation ("com.airbnb.android:lottie-compose:6.1.0") // 최신 버전 확인 후 적용

}