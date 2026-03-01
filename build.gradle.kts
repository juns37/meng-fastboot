// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false       // upgrade minimal 8.2.0 biar aman dengan compileSdk 34
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false // sesuaikan dengan Compose 1.5.3
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}