plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.alonso.xmlroom"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.alonso.xmlroom"
        minSdk = 24
        targetSdk = 36
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)

    implementation(libs.glide)

// Estas 3 son las mismas para XML y Compose:
    implementation(libs.androidx.room.runtime)       // Runtime de Room
    ksp(libs.androidx.room.compiler)                // Compilador (genera código)
    implementation(libs.androidx.room.coroutines)    // Soporte para Coroutines

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences) // O la última versión

    implementation(libs.jbcrypt)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.fragment.ktx) // fragments

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}