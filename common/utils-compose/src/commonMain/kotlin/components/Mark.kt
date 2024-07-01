package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import report.UserMark
import server.fetchReason
import view.handy

@Composable
fun MarkContent(
    mark: String,
    background: Color = MaterialTheme.colorScheme.primary.copy(
        alpha = .2f
    ),
    addModifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    paddingValues: PaddingValues = PaddingValues(start = 5.dp, top = 5.dp),
    size: Dp = 25.dp,
    textYOffset: Dp = 0.dp
) {
    Box(
        Modifier.padding(paddingValues)
            .offset(offset.x, offset.y)
            .size(size)
            .clip(RoundedCornerShape(percent = 30))
            .background(
                background
            )
            .then(addModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            mark,
            fontSize = size.value.sp/1.6f,
            modifier = Modifier.fillMaxSize().offset(y = textYOffset),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cMark(mark: UserMark, coroutineScope: CoroutineScope, showDate: Boolean = true) {
    val markSize = 30.dp
    val yOffset = 3.dp
    val tState = rememberTooltipState(isPersistent = false)
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip(modifier = Modifier.clickable {}) {
                Text(" ${if(showDate) "${mark.date}\n" else ""}${fetchReason(mark.reason)}", textAlign = TextAlign.Center)
            }
        },
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
    ) {
        MarkContent(
            mark.content,
            size = markSize,
            textYOffset = yOffset,
            addModifier = Modifier.clickable {
                coroutineScope.launch {
                    tState.show()
                }
            }.handy()
                .pointerInput(PointerEventType.Press) {
                    println("asd")
                }
        )
    }
}