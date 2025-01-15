plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization") version "2.0.21"
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.grocerly"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.grocerly"
        minSdk = 29
        targetSdk = 35
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

    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}

dependencies {



    //navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.androidx.core.splashscreen)
    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    //livedata

        // ViewModel
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        // ViewModel utilities for Compose
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        // LiveData
        implementation(libs.androidx.lifecycle.livedata.ktx)

        // Saved state module for ViewModel
        implementation(libs.androidx.lifecycle.viewmodel.savedstate)

        // Annotation processor
        kapt(libs.androidx.lifecycle.compiler)

    //dagger and hilt

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //glide
    implementation(libs.glide)

    //datastore
    implementation(libs.androidx.datastore.preferences)


    //lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //coroutines
    implementation(libs.kotlinx.coroutines.android)




    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}