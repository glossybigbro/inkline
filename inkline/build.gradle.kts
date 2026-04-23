plugins {
    alias(libs.plugins.android.library) // ① 이 모듈은 Android 라이브러리
    alias(libs.plugins.compose.compiler) // ② Compose 코드 쓸 거야
    alias(libs.plugins.maven.publish) // ③ Maven Central에 배포할 거야
}

android {
    namespace = "io.github.glossybigbro.inkline" // 패키지명 (R 클래스 등에 사용)
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt() // 36 — 컴파일에 사용할 SDK

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt() // 23 — 최소 지원 버전
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Java 17 문법 허용
        targetCompatibility = JavaVersion.VERSION_17 // Java 17 바이트코드 생성
    }

    buildFeatures {
        compose = true // Compose 활성화
    }
}

dependencies {
    // BOM = Bill of Materials (부품 목록)
    // Compose 라이브러리들의 버전을 BOM이 통합 관리
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)

    testImplementation(libs.junit)
    // 버전 안 써도 됨 — BOM이 알아서 맞는 버전 넣어줌
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// dependencies {
//    implementation(libs.androidx.compose.ui)      // ① 내부에서만 사용
//    api(libs.androidx.compose.foundation)          // ② 외부에도 노출
//    testImplementation(libs.junit)                 // ③ 테스트에서만 사용
//    debugImplementation(libs.androidx.compose.ui.tooling)  // ④ 디버그 빌드에서만
// }
