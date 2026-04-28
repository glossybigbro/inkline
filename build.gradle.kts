plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.spotless)
}

// clone 시 git hook 경로 자동 설정
tasks.register<Exec>("installGitHooks") {
    commandLine("git", "config", "core.hooksPath", ".githooks")
    // CI 환경에서는 실패해도 무시
    isIgnoreExitValue = true
}

// 빌드 시 자동으로 git hook 설정
tasks.matching { it.name == "prepareKotlinBuildScriptModel" }.configureEach {
    dependsOn("installGitHooks")
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
    }
}
