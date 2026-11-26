package amu.wmi.eyelearner.app.theme

//region imports
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.Easing
import kotlin.math.cos
import kotlin.math.PI
import androidx.compose.animation.core.StartOffset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.BiasAlignment

//endregion imports


//region Creature

//Built creatures
    @Composable fun MenuCreature(
        heightMin: Float = 0.6f,
        heightMax: Float = 0.7f,
        modifier: Modifier
    ){
        CreatureBody(
            heightMin = heightMin,
            heightMax = heightMax,
            topLeftCornerMin = 70.dp,
            topLeftCornerMax = 75.dp,
            topRightCornerMin = 60.dp,
            topRightCornerMax = 65.dp,
            width = 150.dp,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DoubleEye(
                    diameter = 60.dp,
                    insideColor = LocalThemeManager.current.creatureTheme.eye_color,
                    offsetX = -(4.dp),
                    offsetY = -(4.dp),
                    modifier = Modifier.offset(x = -(15.dp))
                )
            }
        }
    }



//region creature animation
private val PI_F = PI.toFloat()
private val SineInOut = Easing { t -> (1f - cos(PI_F * t)) / 2f}

@Composable private fun rememberSine(
    min: Float,
    max: Float,
    periodMillis: Int,
    phase: Float = 0f,
    enabled: Boolean = true,
    label: String = "sine"
): Float {
    if (!enabled || periodMillis <= 0 || min == max) return max

    val transition = rememberInfiniteTransition(label = label)
    val value by transition.animateFloat(
        initialValue = min,
        targetValue  = max,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = periodMillis / 2,
                easing = SineInOut
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(
                offsetMillis = ((phase % 1f) * periodMillis).toInt()
            )
        ),
        label = "$label.value"
    )
    return value
}
//endregion

//Build creature
    @Composable fun CreatureBody(

        // Height (fraction of parent)
        heightMin: Float = 0.6f,
        heightMax: Float = 0.7f,

        // Top-left corner radius
        topLeftCornerMin:  Dp = 120.dp,
        topLeftCornerMax:  Dp = 140.dp,

        // Top-right corner radius
        topRightCornerMin: Dp = 120.dp,
        topRightCornerMax: Dp = 140.dp,

        width: Dp = 0.dp,

        breathe: Boolean = true,
        periodMillis: Int = 1400,

        topLeftPhase:  Float = 0f,
        topRightPhase: Float = 0f,

        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        // ---- Height animation ----
        val heightFraction = rememberSine(
            min = heightMin,
            max = heightMax,
            periodMillis = periodMillis,
            phase = 0f,
            enabled = breathe,
            label = "creature.height"
        )

        // ---- Top-left corner animation ----
        val topLeftRadius = rememberSine(
            min = topLeftCornerMin.value,
            max = topLeftCornerMax.value,
            periodMillis = periodMillis,
            phase = topLeftPhase,
            enabled = breathe,
            label = "creature.topLeft"
        )

        // ---- Top-right corner animation ----
        val topRightRadius = rememberSine(
            min = topRightCornerMin.value,
            max = topRightCornerMax.value,
            periodMillis = periodMillis,
            phase = topRightPhase,
            enabled = breathe,
            label = "creature.topRight"
        )

        Card(
            modifier = modifier
                .fillMaxHeight(heightFraction)
                .width(width),

            shape = RoundedCornerShape(
                topStart = topLeftRadius.dp,
                topEnd   = topRightRadius.dp,
            ),

            colors = CardDefaults.cardColors(containerColor = LocalThemeManager.current.creatureTheme.body_color),

            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            content()
        }
    }

    //region Eye

    //region Eye elements
    @Composable
    fun EyeBody(
        outsideDiameter: Dp,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit = {}
    ) {
        Box(
            modifier = modifier
                .size(outsideDiameter)
                .background(color = Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) { content() }
    }

    @Composable
    fun Pupil(
        insideDiameter: Dp,
        insideColor: Color,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 0.dp,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier // <-- Use the modifier passed in
                .offset(x = offsetX, y = offsetY)
                .size(insideDiameter)
                .background(color = insideColor, shape = CircleShape)
        )
    }
    //endregion

    fun pupilSize(size : Dp) : Dp {
        return size/2f
    }

    @Composable fun Eye(
        diameter: Dp,
        insideColor: Color,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 0.dp,
        modifier: Modifier = Modifier
    ) {
        val insideDiameter : Dp = pupilSize(diameter)

        EyeBody(
            outsideDiameter = diameter,
            modifier = modifier,
        ) {
            Pupil(
                insideDiameter = insideDiameter,
                insideColor = insideColor,
                offsetX = offsetX,
                offsetY = offsetY,
            )
        }
    }

    @Composable
    fun DoubleEye(
        diameter: Dp,
        insideColor: Color,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 0.dp,
        modifier: Modifier = Modifier
    ) {
        val insideDiameter : Dp = pupilSize(diameter)

        val leftOuter = diameter - diameter / 3
        val leftInner = insideDiameter - insideDiameter / 3

        val rightEyeOffset = leftOuter * 0.75f
        val leftEyeVerticalOffset = 10.dp

        val totalWidth = rightEyeOffset + diameter
        val totalHeight = diameter + leftEyeVerticalOffset

        Box(
            modifier = modifier
                .size(width = totalWidth, height = totalHeight),
            contentAlignment = Alignment.TopStart
        ) {

            // Left Eye Background
            EyeBody(
                outsideDiameter = leftOuter,
                modifier = Modifier
                    .offset(y = leftEyeVerticalOffset)
                    .zIndex(1f)
            )

            // Right Eye Background
            EyeBody(
                outsideDiameter = diameter,
                modifier = Modifier
                    .offset(x = rightEyeOffset)
                    .zIndex(1f)
            )

            // Left Pupil
            val leftPupilCenterOffset = (leftOuter - leftInner) / 2
            Pupil(
                insideDiameter = leftInner,
                insideColor = insideColor,
                offsetX = offsetX / 3 + leftPupilCenterOffset,
                offsetY = (offsetY / 3 + leftPupilCenterOffset) + leftEyeVerticalOffset,
                modifier = Modifier.zIndex(2f)
            )

            // Right Pupil
            val rightPupilCenterOffset = (diameter - insideDiameter) / 2
            Pupil(
                insideDiameter = insideDiameter,
                insideColor = insideColor,
                offsetX = offsetX + rightEyeOffset + rightPupilCenterOffset,
                offsetY = offsetY + rightPupilCenterOffset,
                modifier = Modifier.zIndex(2f)
            )
        }
    }



    //endregion

//endregion


//region Background
@Composable
fun Background(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val squareColors = LocalThemeManager.current.creatureTheme.backgroundBody_color

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalThemeManager.current.creatureTheme.background_color)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {

            val biggerSquareSize = size.minDimension * 0.5f
            val smallerSquareSize = size.minDimension * 0.36f
            val margin     = 50.dp.toPx()
            val corner     = 80.dp.toPx()

            drawRoundRect(
                color = squareColors,
                topLeft = Offset(margin, margin),
                size = Size(smallerSquareSize, smallerSquareSize),
                cornerRadius = CornerRadius(corner, corner)
            )

            drawRoundRect(
                color = squareColors,
                topLeft = Offset(
                    x = size.width  - margin - biggerSquareSize,
                    y = size.height - margin - biggerSquareSize
                ),
                size = Size(biggerSquareSize, biggerSquareSize),
                cornerRadius = CornerRadius(corner, corner)
            )
        }

        content()
    }
}
//endregion

