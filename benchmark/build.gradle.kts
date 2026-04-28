plugins {
    alias(libs.plugins.android.test)
}

android {
    namespace = "io.github.glossybigbro.inkline.benchmark"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":sample"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.benchmark.macro.junit4)
    implementation(libs.test.ext.junit)
    implementation(libs.uiautomator)
}
