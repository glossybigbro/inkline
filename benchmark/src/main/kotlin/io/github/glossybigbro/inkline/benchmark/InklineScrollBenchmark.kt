package io.github.glossybigbro.inkline.benchmark

import android.content.Intent
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InklineScrollBenchmark {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun scroll_inkline_solid() = scroll(mode = "inkline", style = "solid")

    @Test
    fun scroll_inkline_wavy() = scroll(mode = "inkline", style = "wavy")

    @Test
    fun scroll_native() = scroll(mode = "native")

    private fun scroll(
        mode: String,
        style: String = "solid",
    ) {
        rule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.WARM,
            setupBlock = {
                startActivityAndWait(
                    Intent(ACTION_BENCHMARK).apply {
                        putExtra("mode", mode)
                        putExtra("lines", 100)
                        putExtra("style", style)
                    },
                )
            },
        ) {
            val list = device.findObject(By.res(PACKAGE_NAME, "list"))
            list.setGestureMargin(device.displayWidth / 5)
            list.fling(Direction.DOWN)
            device.waitForIdle()
        }
    }
}
