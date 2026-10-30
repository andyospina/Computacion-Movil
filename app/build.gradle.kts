plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.ksp)
    //Messaging
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)

}

android {
    namespace = "com.example.myapplication"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Debug usa Firebase Emulator Suite por defecto. Para probar Firebase real en debug:
            // ./gradlew installDebug -PuseEmulator=false
            val useEmulator = providers.gradleProperty("useEmulator").getOrElse("true")
            buildConfigField("boolean", "USE_FIREBASE_EMULATOR", useEmulator)
        }
        release {
            buildConfigField("boolean", "USE_FIREBASE_EMULATOR", "false")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.benchmark.traceprocessor)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    //Hilt
    implementation(libs.dagger.hilt)
    implementation(libs.hilt.compose.navigation)
    ksp(libs.dagger.kapt)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.kotlinx.coroutines.play.services)

    //Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Datos ficticios (solo debug: el seeder y sus pruebas no viajan en release)
    debugImplementation(libs.javafaker) { exclude(group = "org.yaml", module = "snakeyaml") }
    // JavaFaker necesita la variante Android de SnakeYAML
    debugImplementation("${libs.snakeyaml.android.get()}:android")

    // Pruebas de integración contra Firebase Emulator Suite
    androidTestImplementation(libs.androidx.test.runner)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
}