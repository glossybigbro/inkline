package io.github.glossybigbro.inkline.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.glossybigbro.inkline.InklineStyle
import io.github.glossybigbro.inkline.drawBehind
import io.github.glossybigbro.inkline.rememberInkline

@OptIn(ExperimentalComposeUiApi::class)
class BenchmarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mode = intent.getStringExtra("mode") ?: "inkline"
        val lines = intent.getIntExtra("lines", 1)
        val style = intent.getStringExtra("style") ?: "solid"

        setContent {
            MaterialTheme {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                            .semantics { testTagsAsResourceId = true },
                ) {
                    BenchmarkContent(mode = mode, lines = lines, style = style)
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun BenchmarkContent(
    mode: String,
    lines: Int,
    style: String,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("list"),
    ) {
        items(lines) { index ->
            when (mode) {
                "native" -> NativeItem(index)
                else -> InklineItem(index, style)
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun InklineItem(
    index: Int,
    style: String,
) {
    val inklineStyle =
        when (style) {
            "wavy" -> InklineStyle.Wavy
            "dashed" -> InklineStyle.Dashed
            "dotted" -> InklineStyle.Dotted
            else -> InklineStyle.Solid
        }
    val inkline =
        rememberInkline {
            underline(offset = 4.dp, color = Color.Blue, style = inklineStyle)
        }
    Text(
        text = inkline.apply("Benchmark line $index with underline"),
        modifier =
            Modifier
                .padding(vertical = 4.dp)
                .drawBehind(inkline),
        onTextLayout = inkline::onTextLayout,
        fontSize = 16.sp,
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun NativeItem(index: Int) {
    Text(
        text = "Benchmark line $index with underline",
        modifier = Modifier.padding(vertical = 4.dp),
        style =
            TextStyle(
                textDecoration = TextDecoration.Underline,
                fontSize = 16.sp,
            ),
    )
}
