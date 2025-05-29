package com.nermingules.walktroughsetepbystep

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.serialization.Serializable

@Serializable
data class WalkthroughStep(
    val id: String,
    val title: String,
    val description: String,
    val targetKey: String,
    val buttonText: String? = null
)

data class TargetPosition(
    val offset: Offset,
    val size: Size
)

data class WalkthroughConfig(
    val overlayColor: Color = Color.Black.copy(alpha = 0.7f),
    val highlightPadding: Float = 16f,
    val highlightCornerRadius: Float = 12f,
    val cardMaxWidth: Float = 280f,
    val cardBackgroundColor: Color = Color.White,
    val cardElevation: Float = 8f,
    val animationDuration: Int = 300,
    val nextButtonText: String = "İleri",
    val previousButtonText: String = "Geri",
    val finishButtonText: String = "Tamam",
    val stepCounterFormat: String = "({current}/{total})"
)

@Composable
fun rememberWalkthroughState(
    steps: List<WalkthroughStep>,
    autoStart: Boolean = false
): WalkthroughState {
    return remember(steps) {
        WalkthroughState(steps, autoStart)
    }
}

class WalkthroughState(
    internal val steps: List<WalkthroughStep>,
    autoStart: Boolean = false
) {
    var isVisible by mutableStateOf(autoStart)
        internal set

    var currentStepIndex by mutableIntStateOf(0)
        internal set

    var targetPositions by mutableStateOf<Map<String, TargetPosition>>(emptyMap())
        internal set

    val currentStep: WalkthroughStep?
        get() = steps.getOrNull(currentStepIndex)

    val hasNext: Boolean
        get() = currentStepIndex < steps.size - 1

    val hasPrevious: Boolean
        get() = currentStepIndex > 0

    val isLastStep: Boolean
        get() = currentStepIndex == steps.size - 1

    fun start() {
        currentStepIndex = 0
        isVisible = true
    }

    fun next() {
        if (hasNext) {
            currentStepIndex++
        }
    }

    fun previous() {
        if (hasPrevious) {
            currentStepIndex--
        }
    }

    fun finish() {
        isVisible = false
        currentStepIndex = 0
        targetPositions = emptyMap()
    }

    fun skip() {
        finish()
    }

    fun goToStep(stepIndex: Int) {
        if (stepIndex in 0 until steps.size) {
            currentStepIndex = stepIndex
        }
    }

    internal fun updateTargetPosition(key: String, position: TargetPosition) {
        targetPositions = targetPositions + (key to position)
    }
}

@Composable
fun WalkthroughTarget(
    key: String,
    walkthroughState: WalkthroughState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            val position = TargetPosition(
                offset = coordinates.positionInRoot(),
                size = Size(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat()
                )
            )
            walkthroughState.updateTargetPosition(key, position)
        }
    ) {
        content()
    }
}

@Composable
fun WalkthroughOverlay(
    walkthroughState: WalkthroughState,
    config: WalkthroughConfig = WalkthroughConfig(),
    modifier: Modifier = Modifier,
    onStepChange: (WalkthroughStep) -> Unit = {},
    onFinish: () -> Unit = {},
    customCard: @Composable ((WalkthroughStep, Int, Int) -> Unit)? = null
) {
    val density = LocalDensity.current
    val animatedAlpha by animateFloatAsState(
        targetValue = if (walkthroughState.isVisible) 1f else 0f,
        animationSpec = tween(config.animationDuration),
        label = "overlay_alpha"
    )

    val canvasSize = remember { mutableStateOf(Size.Zero) }

    if (walkthroughState.isVisible) {
        walkthroughState.currentStep?.let { currentStep ->
            LaunchedEffect(currentStep) {
                onStepChange(currentStep)
            }

            val targetPosition = walkthroughState.targetPositions[currentStep.targetKey]

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .zIndex(1000f)
                    .graphicsLayer(alpha = animatedAlpha)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            canvasSize.value = Size(it.width.toFloat(), it.height.toFloat())
                        }
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                ) {
                    drawRect(
                        color = config.overlayColor,
                        size = this.size
                    )

                    targetPosition?.let { pos ->
                        val padding = config.highlightPadding
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
                            cornerRadius = CornerRadius(config.highlightCornerRadius),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                targetPosition?.let { pos ->
                    val cardOffset = calculateCardPosition(
                        targetPosition = pos,
                        canvasSize = canvasSize.value,
                        config = config
                    )

                    if (customCard != null) {
                        Box(
                            modifier = Modifier.offset(
                                x = with(density) { cardOffset.x.toDp() },
                                y = with(density) { cardOffset.y.toDp() }
                            )
                        ) {
                            customCard(
                                currentStep,
                                walkthroughState.currentStepIndex + 1,
                                walkthroughState.steps.size
                            )
                        }
                    } else {
                        DefaultWalkthroughCard(
                            step = currentStep,
                            currentIndex = walkthroughState.currentStepIndex + 1,
                            totalSteps = walkthroughState.steps.size,
                            offset = cardOffset,
                            config = config,
                            hasNext = walkthroughState.hasNext,
                            hasPrevious = walkthroughState.hasPrevious,
                            isLastStep = walkthroughState.isLastStep,
                            onNext = walkthroughState::next,
                            onPrevious = walkthroughState::previous,
                            onFinish = {
                                walkthroughState.finish()
                                onFinish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultWalkthroughCard(
    step: WalkthroughStep,
    currentIndex: Int,
    totalSteps: Int,
    offset: Offset,
    config: WalkthroughConfig,
    hasNext: Boolean,
    hasPrevious: Boolean,
    isLastStep: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinish: () -> Unit
) {
    val density = LocalDensity.current

    var cardHeight by remember { mutableStateOf(0f) }

    Card(
        modifier = Modifier
            .offset(
                y = with(density) { offset.y.toDp() }
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .widthIn(max = config.cardMaxWidth.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = config.cardElevation.dp),
        colors = CardDefaults.cardColors(containerColor = config.cardBackgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${step.title} ${config.stepCounterFormat.replace("{current}", currentIndex.toString()).replace("{total}", totalSteps.toString())}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = step.description,
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
                if (hasPrevious) {
                    IconButton(onClick = onPrevious) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = config.previousButtonText,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                if (hasNext) {
                    IconButton(onClick = onNext) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = config.nextButtonText,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    TextButton(
                        onClick = onFinish,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(step.buttonText ?: config.finishButtonText)
                    }
                }
            }
        }
    }
}

private fun calculateCardPosition(
    targetPosition: TargetPosition,
    canvasSize: Size,
    config: WalkthroughConfig
): Offset {
    val cardHeight = 250f
    val screenMargin = 16f * 4f
    val spacingFromHighlight = 32f

    if (canvasSize.width <= 0 || canvasSize.height <= 0) {
        return Offset(screenMargin, screenMargin)
    }

    // Highlight alanının gerçek sınırları
    val highlightTop = targetPosition.offset.y - config.highlightPadding
    val highlightBottom = targetPosition.offset.y + targetPosition.size.height + config.highlightPadding
    val highlightCenterY = (highlightTop + highlightBottom) / 2f
    val canvasCenterY = canvasSize.height / 2f

    val cardX = (canvasSize.width - config.cardMaxWidth) / 2f

    // Yukarıda ve aşağıda kullanılabilir alanları hesapla
    val availableSpaceAbove = highlightTop - screenMargin
    val availableSpaceBelow = canvasSize.height - highlightBottom - screenMargin
    val totalRequiredSpace = cardHeight + spacingFromHighlight

    // Debug log ekleyebilirsin
    println("HighlightTop: $highlightTop, HighlightBottom: $highlightBottom")
    println("AvailableSpaceAbove: $availableSpaceAbove, AvailableSpaceBelow: $availableSpaceBelow")
    println("RequiredSpace: $totalRequiredSpace")

    val cardY = when {
        // Eğer highlight alt yarıda ve yukarıda yeterli yer varsa -> yukarı yerleştir
        highlightCenterY > canvasCenterY && availableSpaceAbove >= totalRequiredSpace -> {
            highlightTop - cardHeight - spacingFromHighlight
        }
        // Eğer highlight üst yarıda ve aşağıda yeterli yer varsa -> aşağı yerleştir
        highlightCenterY <= canvasCenterY && availableSpaceBelow >= totalRequiredSpace -> {
            highlightBottom + spacingFromHighlight
        }
        // Hangi tarafta daha fazla yer varsa oraya yerleştir
        availableSpaceAbove >= availableSpaceBelow -> {
            // Yukarıya yerleştir, ama minimum screenMargin bırak
            maxOf(screenMargin, highlightTop - cardHeight - spacingFromHighlight)
        }
        else -> {
            // Aşağıya yerleştir, ama ekran dışına taşma
            minOf(
                highlightBottom + spacingFromHighlight,
                canvasSize.height - cardHeight - screenMargin
            )
        }
    }

    return Offset(cardX, cardY)
}


@Composable
fun rememberWalkthroughStepsFromAssets(
    fileName: String,
    fallbackSteps: List<WalkthroughStep> = emptyList()
): List<WalkthroughStep> {
    var steps by remember { mutableStateOf(fallbackSteps) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(fileName) {
        try {
            val inputStream = context.assets.open(fileName)
            val jsonText = java.io.InputStreamReader(inputStream).readText()
            steps = kotlinx.serialization.json.Json.decodeFromString<List<WalkthroughStep>>(jsonText)
        } catch (e: Exception) {
            // Fallback steps kullanılır
            steps = fallbackSteps
        }
    }
    return steps
}

fun WalkthroughState.startFrom(stepId: String) {
    val index = steps.indexOfFirst { it.id == stepId }
    if (index != -1) {
        currentStepIndex = index
        isVisible = true
    }
}

fun WalkthroughState.addOnCompleteListener(onComplete: () -> Unit): WalkthroughState {
    return this
}