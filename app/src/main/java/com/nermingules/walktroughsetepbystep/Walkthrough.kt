package com.nermingules.walktroughsetepbystep

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

enum class WalkthroughShape {
    Circle,
    RoundedRect,
    Oval
}

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
    val size: Size,
    val shape: WalkthroughShape = WalkthroughShape.RoundedRect
)

@Immutable
data class WalkthroughConfig(
    val overlayColor: Color = Color.Black.copy(alpha = 0.7f),
    val highlightPadding: Float = 16f,
    val highlightCornerRadius: Float = 12f,
    val cardMaxWidth: Float = 280f,
    val cardBackgroundColor: Color = Color.White,
    val cardTextColor: Color = Color.Black,
    val cardElevation: Float = 8f,
    val animationDuration: Int = 300,
    val shape: WalkthroughShape = WalkthroughShape.RoundedRect,
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

@Stable
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
    shape: WalkthroughShape = WalkthroughShape.RoundedRect,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            val position = TargetPosition(
                offset = coordinates.positionInRoot(),
                size = Size(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat()
                ),
                shape = shape
            )
            walkthroughState.updateTargetPosition(key, position)
        }
    ) {
        content()
    }
}

fun DrawScope.drawHighlightShape(
    highlightRect: Rect,
    shape: WalkthroughShape,
    overlayColor: Color,
    highlightCornerRadius: Float
) {
    drawRect(
        color = overlayColor,
        size = size
    )

    when (shape) {
        WalkthroughShape.Circle -> {
            val center = highlightRect.center
            val radius = minOf(highlightRect.width, highlightRect.height) / 2

            drawCircle(
                color = Color.Transparent,
                radius = radius,
                center = center,
                blendMode = BlendMode.Clear
            )
        }

        WalkthroughShape.RoundedRect -> {
            drawRoundRect(
                color = Color.Transparent,
                topLeft = highlightRect.topLeft,
                size = highlightRect.size,
                cornerRadius = CornerRadius(highlightCornerRadius),
                blendMode = BlendMode.Clear
            )
        }


        WalkthroughShape.Oval -> {
            drawOval(
                color = Color.Transparent,
                topLeft = highlightRect.topLeft,
                size = highlightRect.size,
                blendMode = BlendMode.Clear
            )
        }
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

                        drawHighlightShape(
                            highlightRect = highlightRect,
                            shape = pos.shape,
                            overlayColor = config.overlayColor,
                            highlightCornerRadius = config.highlightCornerRadius
                        )
                    } ?: run {
                        drawRect(
                            color = config.overlayColor,
                            size = this.size
                        )
                    }
                }

                targetPosition?.let { pos ->
                    var cardHeightPx by remember { mutableIntStateOf(250) } // fallback
                    val cardOffset = calculateCardPosition(
                        targetPosition = pos,
                        canvasSize = canvasSize.value,
                        config = config,
                        cardHeight = cardHeightPx.toFloat()
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


                        MeasurableWalkthroughCard(
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
                            },
                            onCancelWalcktrough = {
                                walkthroughState.finish()
                                onFinish()
                            },
                            onMeasured = { measured -> cardHeightPx = measured }
                        )
                    }
                }
            }
        }
    }
}

private fun calculateCardPosition(
    targetPosition: TargetPosition,
    canvasSize: Size,
    config: WalkthroughConfig,
    cardHeight: Float
): Offset {
    val spacingFromHighlight = 32f
    val screenMargin = 16f * 4f

    if (canvasSize.width <= 0 || canvasSize.height <= 0) {
        return Offset(screenMargin, screenMargin)
    }

    val highlightTop = targetPosition.offset.y - config.highlightPadding
    val highlightBottom =
        targetPosition.offset.y + targetPosition.size.height + config.highlightPadding
    val highlightCenterY = (highlightTop + highlightBottom) / 2f
    val canvasCenterY = canvasSize.height / 2f

    val cardX = (canvasSize.width - config.cardMaxWidth) / 2f

    val availableSpaceAbove = highlightTop - screenMargin
    val availableSpaceBelow = canvasSize.height - highlightBottom - screenMargin
    val totalRequiredSpace = cardHeight + spacingFromHighlight

    val cardY = when {
        highlightCenterY > canvasCenterY && availableSpaceAbove >= totalRequiredSpace -> {
            highlightTop - cardHeight - spacingFromHighlight
        }

        highlightCenterY <= canvasCenterY && availableSpaceBelow >= totalRequiredSpace -> {
            highlightBottom + spacingFromHighlight
        }

        availableSpaceAbove >= availableSpaceBelow -> {
            maxOf(screenMargin, highlightTop - cardHeight - spacingFromHighlight)
        }

        else -> {
            minOf(
                highlightBottom + spacingFromHighlight,
                canvasSize.height - cardHeight - screenMargin
            )
        }
    }

    return Offset(cardX, cardY)
}

@Composable
private fun MeasurableWalkthroughCard(
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
    onFinish: () -> Unit,
    onCancelWalcktrough: () -> Unit,
    onMeasured: (Int) -> Unit
) {
    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .offset(y = with(density) { offset.y.toDp() })
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .widthIn(max = config.cardMaxWidth.dp)
            .onGloballyPositioned {
                onMeasured(it.size.height)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = config.cardElevation.dp),
        colors = CardDefaults.cardColors(containerColor = config.cardBackgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "${step.title} ${
                        config.stepCounterFormat.replace(
                            "{current}",
                            currentIndex.toString()
                        ).replace("{total}", totalSteps.toString())
                    }",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )


                IconButton(onClick = onCancelWalcktrough) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "cancel Walkthrough"
                    )
                }
            }

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
                            contentDescription = config.previousButtonText
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                if (hasNext) {
                    IconButton(onClick = onNext) {
                        Icon(Icons.Default.ArrowForward, contentDescription = config.nextButtonText)
                    }
                } else {
                    TextButton(onClick = onFinish) {
                        Text(
                            step.buttonText ?: config.finishButtonText,
                            color = config.cardTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun rememberWalkthroughStepsFromAssets(
    fileName: String,
    fallbackSteps: List<WalkthroughStep> = emptyList()
): List<WalkthroughStep> {
    var steps by remember { mutableStateOf(fallbackSteps) }
    val context = LocalContext.current

    LaunchedEffect(fileName) {
        try {
            val inputStream = context.assets.open(fileName)
            val jsonText = InputStreamReader(inputStream).readText()
            steps = Json.decodeFromString<List<WalkthroughStep>>(jsonText)
        } catch (e: Exception) {
            println("Error reading walkthrough steps: ${e.message}")
            steps = fallbackSteps
        }
    }
    return steps
}

//fun WalkthroughState.startFrom(stepId: String) {
//    val index = steps.indexOfFirst { it.id == stepId }
//    if (index != -1) {
//        currentStepIndex = index
//        isVisible = true
//    }
//}
//
