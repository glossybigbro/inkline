package io.github.glossybigbro.inkline.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.glossybigbro.inkline.InklineStyle
import io.github.glossybigbro.inkline.drawBehind
import io.github.glossybigbro.inkline.rememberInkline

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        // Solid
                        val solid =
                            rememberInkline {
                                underline(offset = 4.dp, color = Color.Blue)
                            }
                        Text(
                            text = solid.apply("Solid underline"),
                            modifier = Modifier.drawBehind(solid),
                            onTextLayout = solid::onTextLayout,
                            fontSize = 20.sp,
                        )

                        // Dashed
                        val dashed =
                            rememberInkline {
                                underline(
                                    offset = 4.dp,
                                    style = InklineStyle.Dashed,
                                    color = Color.Red,
                                )
                            }
                        Text(
                            text = dashed.apply("Dashed underline"),
                            modifier = Modifier.drawBehind(dashed),
                            onTextLayout = dashed::onTextLayout,
                            fontSize = 20.sp,
                        )

                        // Dotted
                        val dotted =
                            rememberInkline {
                                underline(
                                    offset = 4.dp,
                                    style = InklineStyle.Dotted,
                                    color = Color.Gray,
                                )
                            }
                        Text(
                            text = dotted.apply("Dotted underline"),
                            modifier = Modifier.drawBehind(dotted),
                            onTextLayout = dotted::onTextLayout,
                            fontSize = 20.sp,
                        )

                        // Wavy
                        val wavy =
                            rememberInkline {
                                underline(
                                    offset = 4.dp,
                                    thickness = 1.5.dp,
                                    style = InklineStyle.Wavy,
                                    color = Color.Red,
                                )
                            }
                        Text(
                            text = wavy.apply("Wavy underline (spelling error)"),
                            modifier = Modifier.drawBehind(wavy),
                            onTextLayout = wavy::onTextLayout,
                            fontSize = 20.sp,
                        )

                        // f vs e 문제 해결 데모
                        val offsetDemo =
                            rememberInkline {
                                underline(offset = 6.dp, thickness = 1.dp)
                            }
                        Text(
                            text = offsetDemo.apply("different offers — f가 e로 안 보임"),
                            modifier = Modifier.drawBehind(offsetDemo),
                            onTextLayout = offsetDemo::onTextLayout,
                            fontSize = 20.sp,
                        )
                    }
                }
            }
        }
    }
}
