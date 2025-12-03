plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // ДЛЯ ЛАБ4:
    id("org.jetbrains.kotlin.kapt")
    // Kotlin serialization plugin for type safe routes and navigation arguments
    kotlin("plugin.serialization") version "2.0.21"
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.succulentus"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.succulentus"
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
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.cardview:cardview:1.0.0")

    // ДЛЯ ЛАБ4: Navigation Component
    val nav_version = "2.7.7" // УБЕРИТЕ ДУБЛИРОВАНИЕ, ОСТАВЬТЕ ТОЛЬКО ЭТУ СТРОКУ

    // Основные зависимости Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Дополнительные зависимости (если нужны)
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Для тестирования (если нужно)
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")


    // JSON serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}