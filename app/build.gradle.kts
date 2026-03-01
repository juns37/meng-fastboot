plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.meng.fastboot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.meng.fastboot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
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
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    
    signingConfigs {
    create("release") {
        storeFile = file("/sdcard/fastboot-release.jks")
        storePassword = "Kebument"
        keyAlias = "fastbootKey"
        keyPassword = "Kebument"
    }
}

buildTypes {
    getByName("release") {
        signingConfig = signingConfigs.getByName("release") // wajib!
        isMinifyEnabled = true
        isShrinkResources = true
    }
}

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")

    // Compose core
    implementation("androidx.compose.ui:ui:1.5.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    
    // Material3
    implementation("androidx.compose.material3:material3:1.2.0")
    
    // Optional: preview tools in IDE
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.3")
}