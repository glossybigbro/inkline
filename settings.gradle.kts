// 플러그인을 어디서 다운로드할지
pluginManagement {
    repositories {
        google() // Android 관련 플러그인 (AGP 등)
        mavenCentral() // 일반 라이브러리
        gradlePluginPortal() // Gradle 전용 플러그인
    }
}

// 라이브러리를 어디서 다운로드할지
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    // FAIL_ON_PROJECT_REPOS = 각 모듈에서 개별 repositories 선언 금지
    // 여기서 한번만 선언하고 전체가 공유
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "inkline" // 프로젝트 이름

include(":inkline") // 라이브러리 모듈 등록
include(":sample") // 샘플 앱 모듈 등록
include(":benchmark") // Macrobenchmark 모듈
