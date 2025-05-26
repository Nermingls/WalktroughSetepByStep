package com.nermingules.walktroughsetepbystep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.nermingules.walktroughsetepbystep.ui.theme.WalktroughSetepByStepTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalktroughSetepByStepTheme {
                ExampleApp()
            }
        }
    }
}
@Serializable
data class WalkthroughStep(
    val title: String,
    val description: String,
    val targetKey: String
)

data class TargetPosition(
    val offset: Offset,
    val size: Size
)

@Composable
fun WalkthroughOverlay(
    steps: List<WalkthroughStep>,
    currentStep: Int,
    targetPositions: Map<String, TargetPosition>,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300),
        label = "overlay_alpha"
    )

    // Canvas boyutunu sakla
    val canvasSize = remember { mutableStateOf(Size.Zero) }

    if (currentStep < steps.size) {
        val currentStepData = steps[currentStep]
        val targetPosition = targetPositions[currentStepData.targetKey]

        Box(
            modifier = modifier
                .fillMaxSize()
                .zIndex(1000f)
                .graphicsLayer(alpha = animatedAlpha)
        ) {
            // Arka plan + delik
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        canvasSize.value = Size(it.width.toFloat(), it.height.toFloat())
                    }
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            ) {
                drawRect(
                    color = Color.Black.copy(alpha = 0.7f),
                    size = this.size
                )

                targetPosition?.let { pos ->
                    val padding = 16.dp.toPx()
                    val highlightRect = Rect(
                        offset = Offset(
                            pos.offset.x - padding,
                            pos.offset.y - padding
                        ),
                        size = Size(
                            pos.size.width + padding * 2,
                            pos.size.height + padding * 2
                        )
                    )

                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = highlightRect.topLeft,
                        size = highlightRect.size,
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        blendMode = BlendMode.Clear
                    )
                }
            }

            // Açıklama kartı
            targetPosition?.let { pos ->
                val cardOffset = calculateCardPosition(
                    targetPosition = pos,
                    canvasSize = canvasSize.value
                )

                Card(
                    modifier = Modifier
                        .offset(
                            x = with(density) { cardOffset.x.toDp() },
                            y = with(density) { cardOffset.y.toDp() }
                        )
                        .widthIn(max = 280.dp)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${currentStepData.title} (${currentStep + 1}/${steps.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentStepData.description,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentStep > 0) {
                                IconButton(onClick = onPrevious) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Önceki",
                                        tint = Color.Blue
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(48.dp))
                            }

                            if (currentStep < steps.size - 1) {
                                IconButton(onClick = onNext) {
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = "Sonraki",
                                        tint = Color.Blue
                                    )
                                }
                            } else {
                                TextButton(
                                    onClick = onFinish,
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Blue)
                                ) {
                                    Text("Başla")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun calculateCardPosition(
    targetPosition: TargetPosition,
    canvasSize: Size
): Offset {
    val cardWidth = 280f
    val cardHeight = 200f
    val margin = 32f

    // Güvenlik kontrolü - canvas boyutu henüz ayarlanmadıysa varsayılan değer kullan
    if (canvasSize.width <= 0 || canvasSize.height <= 0) {
        return Offset(margin, margin)
    }

    val targetCenterX = targetPosition.offset.x + targetPosition.size.width / 2
    val targetCenterY = targetPosition.offset.y + targetPosition.size.height / 2

    // Try to position card below target first
    var cardX = targetCenterX - cardWidth / 2
    var cardY = targetPosition.offset.y + targetPosition.size.height + margin

    // Check if card fits below
    if (cardY + cardHeight > canvasSize.height - margin) {
        // Position above target
        cardY = targetPosition.offset.y - cardHeight - margin
    }

    // Ensure card stays within screen horizontally - güvenli aralık kontrolü
    val minX = margin
    val maxX = (canvasSize.width - cardWidth - margin).coerceAtLeast(margin)
    cardX = if (maxX > minX) {
        cardX.coerceIn(minX, maxX)
    } else {
        minX
    }

    // Ensure card stays within screen vertically - güvenli aralık kontrolü
    val minY = margin
    val maxY = (canvasSize.height - cardHeight - margin).coerceAtLeast(margin)
    cardY = if (maxY > minY) {
        cardY.coerceIn(minY, maxY)
    } else {
        minY
    }

    return Offset(cardX, cardY)
}

@Composable
fun TargetElement(
    key: String,
    onPositionChanged: (String, TargetPosition) -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.onGloballyPositioned { coordinates ->
            val position = TargetPosition(
                offset = coordinates.positionInRoot(),
                size = Size(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat()
                )
            )
            onPositionChanged(key, position)
        }
    ) {
        content()
    }
}

// Example usage
@Composable
fun ExampleApp() {
    var currentStep by remember { mutableIntStateOf(0) }
    var showWalkthrough by remember { mutableStateOf(true) }
    var targetPositions by remember { mutableStateOf<Map<String, TargetPosition>>(emptyMap()) }
    var walkthroughSteps by remember { mutableStateOf<List<WalkthroughStep>>(emptyList()) }

    val context = LocalContext.current

    // JSON'dan walkthrough adımlarını oku
    LaunchedEffect(Unit) {
        try {
            val inputStream = context.assets.open("walkthrough.json")
            val jsonText = InputStreamReader(inputStream).readText()
            walkthroughSteps = Json.decodeFromString<List<WalkthroughStep>>(jsonText)
        } catch (e: Exception) {
            // JSON dosyası yoksa örnek veri kullan
            walkthroughSteps = listOf(
                WalkthroughStep(
                    title = "Profil",
                    description = "Buradan profilinize erişebilirsiniz",
                    targetKey = "profile"
                ),
                WalkthroughStep(
                    title = "Para Transferi",
                    description = "Para transferi işlemlerinizi buradan yapabilirsiniz",
                    targetKey = "transfer"
                ),
                WalkthroughStep(
                    title = "Hızlı İşlemler",
                    description = "En sık kullandığınız işlemlere buradan erişebilirsiniz",
                    targetKey = "quick_actions"
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1976D2))
                .padding(16.dp)
        ) {
            // Top bar with profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TargetElement(
                    key = "profile",
                    onPositionChanged = { key, position ->
                        targetPositions = targetPositions + (key to position)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                }

                Text(
                    text = "200,00 TL",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            TargetElement(
                key = "transfer",
                onPositionChanged = { key, position ->
                    targetPositions = targetPositions + (key to position)
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton("Para\nTransferi", Color.Blue)
                    ActionButton("Fatura\nÖde", Color.Blue)
                    ActionButton("Tüm\nİşlemler", Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quick actions
            TargetElement(
                key = "quick_actions",
                onPositionChanged = { key, position ->
                    targetPositions = targetPositions + (key to position)
                }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Hızlı İşlemler",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "En sık kullandığınız işlemler",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Walkthrough overlay
        if (showWalkthrough && walkthroughSteps.isNotEmpty()) {
            WalkthroughOverlay(
                steps = walkthroughSteps,
                currentStep = currentStep,
                targetPositions = targetPositions,
                onNext = { currentStep++ },
                onPrevious = { currentStep-- },
                onFinish = { showWalkthrough = false }
            )
        }
    }
}

@Composable
private fun ActionButton(text: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            // Icon placeholder
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExampleAppPreview() {
    ExampleApp()
}