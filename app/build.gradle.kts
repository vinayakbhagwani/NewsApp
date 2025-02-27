plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    alias(libs.plugins.dagger)
}

android {
    namespace = "com.vinayak.apps.cardstacksdemoapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vinayak.apps.cardstacksdemoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // paging 3
//    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
//    implementation("androidx.paging:paging-compose:1.0.0-alpha17")

    //navigation
//    implementation("androidx.navigation:navigation-compose:2.4.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0")

    // Coroutine Lifecycle Scopes
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

    //Dagger - Hilt
//    implementation("com.google.dagger:hilt-android:2.50")
//    implementation("com.google.dagger:hilt-android-gradle-plugin:2.50")

    // Dagger - Hilt
//    implementation("com.google.dagger:hilt-android-compiler:2.50")
//    implementation("androidx.hilt:hilt-compiler:1.2.0")
//    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Swipe refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.13-rc")

    // Navigation Component
//    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
//    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
//    implementation("androidx.fragment:fragment-ktx:1.2.5")

    implementation(libs.dagger.hilt)
    implementation(libs.hilt.compose.navigation)
    kapt(libs.dagger.kapt)

    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
}