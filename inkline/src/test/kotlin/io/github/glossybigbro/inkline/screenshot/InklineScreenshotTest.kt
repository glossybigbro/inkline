package io.github.glossybigbro.inkline.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.glossybigbro.inkline.InklineStyle
import io.github.glossybigbro.inkline.drawBehind
import io.github.glossybigbro.inkline.rememberInkline
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class InklineScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val textStyle = TextStyle(fontSize = 20.sp, color = Color.Black)

    @Test
    fun solid_style() {
        composeTestRule.setContent {
            val inkline =
                rememberInkline {
                    underline(offset = 4.dp, color = Color.Blue)
                }
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                BasicText(
                    text = inkline.extend("Solid underline style"),
                    modifier = Modifier.drawBehind(inkline),
                    onTextLayout = inkline::onTextLayout,
                    style = textStyle,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun dashed_style() {
        composeTestRule.setContent {
            val inkline =
                rememberInkline {
                    underline(offset = 4.dp, style = InklineStyle.Dashed, color = Color.Red)
                }
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                BasicText(
                    text = inkline.extend("Dashed underline style"),
                    modifier = Modifier.drawBehind(inkline),
                    onTextLayout = inkline::onTextLayout,
                    style = textStyle,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun dotted_style() {
        composeTestRule.setContent {
            val inkline =
                rememberInkline {
                    underline(offset = 4.dp, style = InklineStyle.Dotted, color = Color.Gray)
                }
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                BasicText(
                    text = inkline.extend("Dotted underline style"),
                    modifier = Modifier.drawBehind(inkline),
                    onTextLayout = inkline::onTextLayout,
                    style = textStyle,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun wavy_style() {
        composeTestRule.setContent {
            val inkline =
                rememberInkline {
                    underline(
                        offset = 4.dp,
                        thickness = 1.5.dp,
                        style = InklineStyle.Wavy,
                        color = Color.Red,
                    )
                }
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                BasicText(
                    text = inkline.extend("Wavy underline style"),
                    modifier = Modifier.drawBehind(inkline),
                    onTextLayout = inkline::onTextLayout,
                    style = textStyle,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun offset_comparison() {
        composeTestRule.setContent {
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                listOf(0.dp, 4.dp, 8.dp).forEach { offset ->
                    val inkline =
                        rememberInkline {
                            underline(offset = offset, color = Color.Blue)
                        }
                    BasicText(
                        text = inkline.extend("offset = $offset"),
                        modifier = Modifier.padding(bottom = 16.dp).drawBehind(inkline),
                        onTextLayout = inkline::onTextLayout,
                        style = textStyle,
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun thickness_comparison() {
        composeTestRule.setContent {
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                listOf(0.5.dp, 1.dp, 2.dp).forEach { thickness ->
                    val inkline =
                        rememberInkline {
                            underline(offset = 4.dp, thickness = thickness, color = Color.Black)
                        }
                    BasicText(
                        text = inkline.extend("thickness = $thickness"),
                        modifier = Modifier.padding(bottom = 16.dp).drawBehind(inkline),
                        onTextLayout = inkline::onTextLayout,
                        style = textStyle,
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun multiline_text() {
        composeTestRule.setContent {
            val inkline =
                rememberInkline {
                    underline(offset = 3.dp, color = Color.Blue)
                }
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                BasicText(
                    text =
                        inkline.extend(
                            "This is a long text that should wrap into multiple lines " +
                                "to verify that Inkline draws underlines on every line correctly.",
                        ),
                    modifier = Modifier.drawBehind(inkline),
                    onTextLayout = inkline::onTextLayout,
                    style = textStyle,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun f_vs_e_problem() {
        composeTestRule.setContent {
            val inkline =
                rememberInkline {
                    underline(offset = 6.dp, thickness = 1.dp, color = Color.Black)
                }
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                BasicText(
                    text = inkline.extend("different offers — f is not e"),
                    modifier = Modifier.drawBehind(inkline),
                    onTextLayout = inkline::onTextLayout,
                    style = textStyle,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
