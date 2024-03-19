package decomposeComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CustomTextButton
import components.LoadingAnimation
import components.cAlertDialog.CAlertDialogComponent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import components.networkInterface.NetworkState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CAlertDialogContent(
    component: CAlertDialogComponent,
    customIf: Boolean? = null,
    isCustomButtons: Boolean = true,
    acceptColor: Color = MaterialTheme.colorScheme.primary,
    title: String = "",
    acceptText: String = "Ок",
    declineText: String = "Отмена",
    content: @Composable (() -> Unit)
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    val isShowing = customIf ?: model.isDialogShowing
    if (isShowing) {
        AlertDialog(
            onDismissRequest = {
                model.onDeclineClick?.invoke()
            },
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .animateContentSize(),
                shape = MaterialTheme.shapes.large
            ) {
                Column {
                    if (title.isNotBlank()) {
                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Crossfade(
                        nModel.state,
                        modifier = Modifier.animateContentSize().heightIn(max = 600.dp)
                            .widthIn(min = TextFieldDefaults.MinWidth)
                    ) {

                        when (it) {
                            NetworkState.None -> {
                                Column(Modifier.padding(6.dp)) {
                                    Box(Modifier.heightIn(max = 350.dp)) {
                                        content()
                                    }
                                    if (!isCustomButtons) {
                                        Row(
                                            Modifier.fillMaxWidth()
                                                .padding(vertical = 10.dp)
                                                .padding(end = 20.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            CustomTextButton(
                                                acceptText,
                                                modifier = Modifier.padding(
                                                    end = 20.dp
                                                ),
                                                color = acceptColor
                                            ) {
                                                model.onAcceptClick?.invoke()
                                            }
                                            CustomTextButton(
                                                declineText
                                            ) {
                                                model.onDeclineClick?.invoke()
                                            }
//                                    AnimatedCommonButton(
//                                        text = "Создать",
//                                        isEnabled = model.isButtonEnabled,
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        if (model.isButtonEnabled) {
//                                            model.onAcceptClick?.invoke()
//                                        }
//                                    }
                                        }
                                    }

                                }
                            }

                            NetworkState.Loading -> {
                                LoadingAnimation()
                            }

                            else -> {
                                Column(
                                    Modifier.width(TextFieldDefaults.MinWidth).padding(6.dp)
                                        .padding(vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(nModel.error)
                                    Spacer(Modifier.height(7.dp))
                                    CustomTextButton("Попробовать ещё раз") {
                                        nModel.onFixErrorClick()
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}