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

//endregion imports


//region Creature
    @Composable fun Creature(
        theme: ThemeManager = LocalThemeManager.current,

        topLeftCorner: Dp = 120.dp,
        topRightCorner: Dp = 140.dp,

        height: Float = 0.7f,
        width: Dp = 0.dp,

        modifier: Modifier = Modifier,
        content: @Composable () -> Unit

    ) {
        Card(
            modifier = modifier
                .fillMaxHeight(height)
                .width(width),

            shape = RoundedCornerShape(
                topStart = topLeftCorner,
                topEnd = topRightCorner,
            ),

            colors = CardDefaults.cardColors(theme.creatureTheme.body_color),

            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            content()
        }
    }

//endregion

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

//region Background
@Composable fun BackGround(
    backgroundColor: Color,
    squareColors: Color,
    content: @Composable () -> Unit
){

//    |   __________                  |
//    |   |        |                  |
//    |   |        |                  |
//    |   |        |                  |
//    |   ‾‾‾‾‾‾‾‾‾‾   ____________
//    |                |          |   |
//    |                |          |   |
//    |                |          |   |
//    |                ‾‾‾‾‾‾‾‾‾‾‾‾   |


}
//endregion

