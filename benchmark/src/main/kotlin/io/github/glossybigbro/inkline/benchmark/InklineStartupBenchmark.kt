package io.github.glossybigbro.inkline.benchmark

import android.content.Intent
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InklineStartupBenchmark {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startup_inkline_singleLine() = startup(mode = "inkline", lines = 1)

    @Test
    fun startup_native_singleLine() = startup(mode = "native", lines = 1)

    @Test
    fun startup_inkline_multiLine() = startup(mode = "inkline", lines = 10)

    @Test
    fun startup_native_multiLine() = startup(mode = "native", lines = 10)

    private fun startup(
        mode: String,
        lines: Int,
    ) {
        rule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.COLD,
        ) {
            pressHome()
            startActivityAndWait(
                Intent(ACTION_BENCHMARK).apply {
                    putExtra("mode", mode)
                    putExtra("lines", lines)
                },
            )
        }
    }
}
